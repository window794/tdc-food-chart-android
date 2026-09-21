package com.broco.tdc_food_chart.data.photo

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * EXIF の日時文字列（"yyyy:MM:dd HH:mm:ss"）を [LocalDateTime] に変換する。
 *
 * EXIF の DateTimeOriginal にはタイムゾーンが含まれない（OffsetTimeOriginal は別タグで、無いことが多い）。
 * 無理に Instant へ変換すると端末のタイムゾーン次第で日付がずれるため、壁時計時刻のまま扱う。
 * 変換できない値（空・"0000:00:00 00:00:00"・壊れた文字列）はすべて null。
 */
object ExifDateParser {
    private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")

    fun parse(raw: String?): LocalDateTime? {
        val text = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        // 一部端末は "yyyy:MM:dd HH:mm:ss.SSS" のように秒以下を付けるので切り落とす
        val normalized = text.substringBefore('.').take(19)
        return try {
            LocalDateTime.parse(normalized, FORMATTER)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
