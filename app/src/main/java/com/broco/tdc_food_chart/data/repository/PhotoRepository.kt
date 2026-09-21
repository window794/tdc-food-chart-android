package com.broco.tdc_food_chart.data.repository

import android.net.Uri
import android.util.Log
import com.broco.tdc_food_chart.data.local.FoodPhotoDao
import com.broco.tdc_food_chart.data.local.FoodPhotoEntity
import com.broco.tdc_food_chart.data.photo.PhotoStorage
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.Clock
import java.time.Instant

/** 写真追加の結果。1 回の選択で複数枚をまとめて扱う。 */
data class PhotoImportResult(
    val added: List<FoodPhotoEntity>,
    val failedCount: Int,
)

/**
 * 写真メタデータ（Room）と実ファイル（内部ストレージ）をまとめて扱う。
 * Room にはパスとメタデータのみを保存し、画像バイナリは保存しない。
 */
class PhotoRepository(
    private val dao: FoodPhotoDao,
    private val storage: PhotoStorage,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    fun observeForFood(foodId: String): Flow<List<FoodPhotoEntity>> = dao.observeForFood(foodId)

    fun fileOf(photo: FoodPhotoEntity): File = storage.fileOf(photo.localPath)

    /**
     * Photo Picker で選んだ URI をアプリ専用領域へ取り込む。
     * 1 枚の失敗で全体を止めず、成功した分だけ登録する。
     */
    suspend fun addPhotos(foodId: String, uris: List<Uri>): PhotoImportResult {
        val added = mutableListOf<FoodPhotoEntity>()
        var failed = 0
        var sortOrder = dao.maxSortOrder(foodId) + 1
        for (uri in uris) {
            try {
                val imported = storage.import(uri, foodId)
                val entity = FoodPhotoEntity(
                    foodId = foodId,
                    localPath = imported.relativePath,
                    mimeType = imported.mimeType,
                    createdAt = Instant.now(clock),
                    capturedAt = imported.capturedAt,
                    sortOrder = sortOrder++,
                )
                val id = try {
                    dao.insert(entity)
                } catch (e: Exception) {
                    // DB 登録に失敗したらコピー済みファイルを残さない
                    storage.delete(imported.relativePath)
                    throw e
                }
                added += entity.copy(id = id)
            } catch (e: Exception) {
                Log.w(TAG, "photo import failed: ${e.message}")
                failed++
            }
        }
        return PhotoImportResult(added, failed)
    }

    /**
     * 写真を削除する。実ファイル → Room レコードの順に消す。
     * ファイル削除に失敗した場合はレコードも残す（壊れたサムネイルより「消えていない」ほうが分かりやすい）。
     */
    suspend fun deletePhoto(photo: FoodPhotoEntity): Boolean {
        val fileDeleted = storage.delete(photo.localPath)
        if (!fileDeleted) {
            Log.w(TAG, "photo file delete failed: ${photo.localPath}")
            return false
        }
        dao.delete(photo)
        return true
    }

    private companion object {
        const val TAG = "PhotoRepository"
    }
}
