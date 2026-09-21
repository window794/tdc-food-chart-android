package com.broco.tdc_food_chart.data.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class ConvertersTest {
    private val c = Converters()

    @Test
    fun localDateRoundTripsAsIsoString() {
        val d = LocalDate.of(2026, 9, 20)
        assertEquals("2026-09-20", c.localDateToString(d))
        assertEquals(d, c.stringToLocalDate("2026-09-20"))
    }

    @Test
    fun localDateNullAndInvalid() {
        assertNull(c.localDateToString(null))
        assertNull(c.stringToLocalDate(null))
        assertNull(c.stringToLocalDate("not a date"))
        assertNull(c.stringToLocalDate("1700000000"))
    }

    @Test
    fun localDateTimeRoundTrips() {
        val dt = LocalDateTime.of(2026, 9, 20, 12, 34, 56)
        val s = c.localDateTimeToString(dt)
        assertEquals("2026-09-20T12:34:56", s)
        assertEquals(dt, c.stringToLocalDateTime(s))
        assertNull(c.stringToLocalDateTime("2026:09:20 12:34:56"))
    }

    @Test
    fun instantRoundTrips() {
        val i = Instant.ofEpochMilli(1_700_000_000_123L)
        assertEquals(1_700_000_000_123L, c.instantToLong(i))
        assertEquals(i, c.longToInstant(1_700_000_000_123L))
        assertNull(c.instantToLong(null))
    }
}
