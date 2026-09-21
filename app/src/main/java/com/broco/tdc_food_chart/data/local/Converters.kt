package com.broco.tdc_food_chart.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Room の型変換。
 * - LocalDate ↔ "yyyy-MM-dd"（ISO）。エポック秒等にするとタイムゾーンで日付がずれる可能性があるので文字列で保持する。
 * - LocalDateTime ↔ ISO-8601（タイムゾーンなし）。EXIF 撮影日時用。
 * - Instant ↔ epoch millis。
 */
class Converters {
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let {
        try {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun localDateTimeToString(value: LocalDateTime?): String? = value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? = value?.let {
        try {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun instantToLong(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun longToInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }
}
