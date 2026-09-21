package com.broco.tdc_food_chart.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

/** 内部ストレージへ取り込んだ写真の情報。 */
data class ImportedPhoto(
    /** filesDir からの相対パス（例: food_photos/abcd/uuid.jpg）。 */
    val relativePath: String,
    val mimeType: String,
    /** EXIF から取得できた撮影日時。無ければ null。 */
    val capturedAt: LocalDateTime?,
)

/**
 * 写真ファイルの保存先を管理する。
 *
 * Photo Picker の URI は一時的なアクセス権しか持たないため長期保存の正本にはせず、
 * `filesDir/food_photos/{foodId}/{uuid}.{ext}` へコピーする。
 * - 元画像を後からギャラリーで消してもアプリ内の記録は残る
 * - 共有ストレージにコピーを作らない
 * - アンインストールで一緒に消える
 */
class PhotoStorage(context: Context) {
    private val appContext = context.applicationContext
    private val rootDir: File get() = File(appContext.filesDir, ROOT_DIR)

    fun fileOf(relativePath: String): File = File(appContext.filesDir, relativePath)

    /**
     * [uri] の画像を [foodId] 用ディレクトリへコピーし、EXIF 撮影日時を読み取る。
     * EXIF の取得失敗は写真追加全体のエラーにしない（capturedAt = null で続行）。
     */
    suspend fun import(uri: Uri, foodId: String): ImportedPhoto = withContext(Dispatchers.IO) {
        val resolver = appContext.contentResolver
        val capturedAt = readCapturedAt(uri)

        val dir = File(rootDir, foodId).apply { mkdirs() }
        val input = resolver.openInputStream(uri) ?: throw IOException("画像を開けませんでした")
        BufferedInputStream(input, 64 * 1024).use { stream ->
            stream.mark(HEADER_SIZE)
            val header = ByteArray(HEADER_SIZE)
            var read = 0
            while (read < HEADER_SIZE) {
                val n = stream.read(header, read, HEADER_SIZE - read)
                if (n < 0) break
                read += n
            }
            stream.reset()

            val format = ImageFormat.resolve(resolver.getType(uri), header.copyOf(read))
            if (format != null) {
                // 元形式のままコピー（再圧縮しない）
                val file = File(dir, "${UUID.randomUUID()}.${format.extension}")
                try {
                    FileOutputStream(file).use { out -> stream.copyTo(out) }
                } catch (e: IOException) {
                    file.delete()
                    throw e
                }
                ImportedPhoto(relativePath(file), format.mimeType, capturedAt)
            } else {
                // 形式を特定できない場合だけ、明示的に JPEG へ変換して保存する
                val file = File(dir, "${UUID.randomUUID()}.jpg")
                try {
                    val bitmap = decodeBitmap(uri)
                    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out) }
                    bitmap.recycle()
                } catch (e: Exception) {
                    file.delete()
                    throw IOException("対応していない画像形式です", e)
                }
                ImportedPhoto(relativePath(file), ImageFormat.JPEG.mimeType, capturedAt)
            }
        }
    }

    /** 実ファイルを削除する。存在しなければ成功扱い。 */
    suspend fun delete(relativePath: String): Boolean = withContext(Dispatchers.IO) {
        val file = fileOf(relativePath)
        if (!file.exists()) true else file.delete()
    }

    private fun decodeBitmap(uri: Uri): Bitmap {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(appContext.contentResolver, uri)
            return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = false
            }
        }
        // API 26–27 は BitmapFactory にフォールバック
        val bitmap = appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        return bitmap ?: throw IOException("画像をデコードできませんでした")
    }

    private fun readCapturedAt(uri: Uri): LocalDateTime? = try {
        appContext.contentResolver.openInputStream(uri)?.use { stream ->
            val exif = ExifInterface(stream)
            val raw = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
            ExifDateParser.parse(raw)
        }
    } catch (e: Exception) {
        // EXIF が無い・壊れている・形式が非対応でもエラーにしない
        Log.d(TAG, "EXIF read failed: ${e.message}")
        null
    }

    private fun relativePath(file: File): String =
        file.relativeTo(appContext.filesDir).path.replace(File.separatorChar, '/')

    private companion object {
        const val TAG = "PhotoStorage"
        const val ROOT_DIR = "food_photos"
        const val HEADER_SIZE = 16
    }
}
