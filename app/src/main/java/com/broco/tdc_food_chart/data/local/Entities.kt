package com.broco.tdc_food_chart.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * ユーザーのローカル記録（料理 1 件につき最大 1 行）。
 * 料理マスター（JSON）とは分離しており、マスター更新時も消えない。
 *
 * [eatenDate] は「食べた日」＝日付のみ。タイムゾーン変換でずれないよう [LocalDate] で扱い、
 * Room には "yyyy-MM-dd" 文字列として保存する（[Converters]）。
 * eaten=false に戻しても eatenDate / memo は消さない（誤操作で記録を失わないため）。
 */
@Entity(tableName = "food_user_record")
data class FoodUserRecordEntity(
    @PrimaryKey @ColumnInfo(name = "food_id") val foodId: String,
    val favorite: Boolean = false,
    val eaten: Boolean = false,
    @ColumnInfo(name = "eaten_date") val eatenDate: LocalDate? = null,
    val memo: String = "",
    @ColumnInfo(name = "updated_at") val updatedAt: Instant = Instant.EPOCH,
) {
    val hasMemo: Boolean get() = memo.isNotBlank()
}

/**
 * ユーザーが追加した写真のメタデータ（料理 1 件につき 0 枚以上）。
 * 画像本体は Room に入れず、アプリ専用領域（filesDir/food_photos/{foodId}/{uuid}.{ext}）に置く。
 *
 * - [localPath]: filesDir からの相対パス。絶対パスを保存しないのは、アプリのデータディレクトリが
 *   端末移行等で変わっても壊れないようにするため。
 * - [createdAt]: ユーザーがこのアプリへ写真を追加した日時（Instant）。
 * - [capturedAt]: EXIF DateTimeOriginal 等から得た撮影日時。EXIF にタイムゾーンがないことが多いので
 *   Instant にせず [LocalDateTime]（壁時計時刻）として保存する。取得できなければ null。
 */
@Entity(
    tableName = "food_photo",
    indices = [Index(value = ["food_id"])],
)
data class FoodPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "food_id") val foodId: String,
    @ColumnInfo(name = "local_path") val localPath: String,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "captured_at") val capturedAt: LocalDateTime? = null,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0,
)
