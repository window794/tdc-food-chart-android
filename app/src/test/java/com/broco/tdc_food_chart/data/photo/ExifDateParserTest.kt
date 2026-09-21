package com.broco.tdc_food_chart.data.photo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class ExifDateParserTest {
    @Test
    fun parsesStandardExifDate() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 34, 56), ExifDateParser.parse("2026:09:20 12:34:56"))
    }

    @Test
    fun ignoresSubSeconds() {
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 34, 56), ExifDateParser.parse("2026:09:20 12:34:56.123"))
    }

    @Test
    fun trimsWhitespace() {
        assertEquals(LocalDateTime.of(2026, 1, 2, 3, 4, 5), ExifDateParser.parse("  2026:01:02 03:04:05 "))
    }

    @Test
    fun nullOrEmptyGivesNull() {
        assertNull(ExifDateParser.parse(null))
        assertNull(ExifDateParser.parse(""))
        assertNull(ExifDateParser.parse("   "))
    }

    @Test
    fun invalidValuesGiveNullInsteadOfThrowing() {
        assertNull(ExifDateParser.parse("0000:00:00 00:00:00"))
        assertNull(ExifDateParser.parse("2026-09-20 12:34:56"))
        assertNull(ExifDateParser.parse("garbage"))
        assertNull(ExifDateParser.parse("2026:13:40 99:99:99"))
    }

    @Test
    fun keepsWallClockTimeWithoutTimezoneShift() {
        // タイムゾーン変換を挟まない＝日付がずれない
        val parsed = ExifDateParser.parse("2026:09:20 23:59:59")
        assertEquals(20, parsed?.dayOfMonth)
        assertEquals(23, parsed?.hour)
    }
}
