package com.broco.tdc_food_chart.domain

import com.broco.tdc_food_chart.TestFoods
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.AreaOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class ObservationStatsTest {
    private val foods = TestFoods.all

    private fun eaten(id: String, date: LocalDate?, updated: Long = 0) =
        FoodUserRecordEntity(foodId = id, eaten = true, eatenDate = date, updatedAt = Instant.ofEpochMilli(updated))

    @Test
    fun emptyRecordsGiveZero() {
        val s = ObservationStats.summarize(foods, emptyMap())
        assertEquals(0, s.eatenCount)
        assertEquals(40, s.totalCount)
        assertEquals(40, s.remaining)
        assertEquals(0, s.percent)
        assertEquals(0f, s.fraction)
        assertEquals(emptyList<RecentObservation>(), s.recent)
    }

    @Test
    fun countsAndPercentAreRounded() {
        val ids = foods.take(12).map { it.id }
        val records = ids.associateWith { eaten(it, LocalDate.of(2026, 9, 1)) }
        val s = ObservationStats.summarize(foods, records)
        assertEquals(12, s.eatenCount)
        assertEquals(28, s.remaining)
        assertEquals(30, s.percent) // 12/40 = 30%
        val one = ObservationStats.summarize(foods, mapOf(ids[0] to eaten(ids[0], null)))
        assertEquals(3, one.percent) // 2.5% → 3
    }

    @Test
    fun favoriteOrMemoWithoutEatenDoesNotCount() {
        val id = foods[0].id
        val records = mapOf(id to FoodUserRecordEntity(foodId = id, favorite = true, memo = "x"))
        assertEquals(0, ObservationStats.summarize(foods, records).eatenCount)
    }

    @Test
    fun areaProgressUsesFixedOrderAndRealTotals() {
        val s = ObservationStats.summarize(foods, emptyMap())
        assertEquals(AreaOrder.ORDER, s.areaProgress.map { it.area })
        assertEquals(40, s.areaProgress.sumOf { it.total })
        s.areaProgress.forEach { ap ->
            assertEquals(foods.count { it.area == ap.area }, ap.total)
            assertEquals(0, ap.eaten)
        }
    }

    @Test
    fun areaProgressCountsEatenPerArea() {
        val hotel = foods.filter { it.area == "東京ドームホテル" }
        val records = hotel.take(2).associate { it.id to eaten(it.id, null) }
        val s = ObservationStats.summarize(foods, records)
        val ap = s.areaProgress.first { it.area == "東京ドームホテル" }
        assertEquals(2, ap.eaten)
        assertEquals(hotel.size, ap.total)
        assertEquals(2f / hotel.size, ap.fraction, 0.0001f)
    }

    @Test
    fun recentIsSortedByEatenDateDescWithNullsLast() {
        val a = foods[0].id
        val b = foods[1].id
        val c = foods[2].id
        val d = foods[3].id
        val records = mapOf(
            a to eaten(a, LocalDate.of(2026, 9, 1)),
            b to eaten(b, LocalDate.of(2026, 9, 18)),
            c to eaten(c, null, updated = 5),
            d to eaten(d, LocalDate.of(2026, 8, 30)),
        )
        val recent = ObservationStats.summarize(foods, records).recent
        assertEquals(listOf(b, a, d, c), recent.map { it.food.id })
        assertNull(recent.last().eatenDate)
    }

    @Test
    fun recentIsLimited() {
        val records = foods.take(8).mapIndexed { i, f -> f.id to eaten(f.id, LocalDate.of(2026, 9, 1 + i)) }.toMap()
        val recent = ObservationStats.summarize(foods, records, recentLimit = 3).recent
        assertEquals(3, recent.size)
        assertEquals(LocalDate.of(2026, 9, 8), recent.first().eatenDate)
    }

    @Test
    fun sameDateOrdersByUpdatedAtDesc() {
        val a = foods[0].id
        val b = foods[1].id
        val date = LocalDate.of(2026, 9, 10)
        val records = mapOf(a to eaten(a, date, updated = 1), b to eaten(b, date, updated = 2))
        val recent = ObservationStats.summarize(foods, records).recent
        assertEquals(listOf(b, a), recent.map { it.food.id })
    }
}
