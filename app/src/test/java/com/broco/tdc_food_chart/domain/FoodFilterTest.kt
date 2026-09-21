package com.broco.tdc_food_chart.domain

import com.broco.tdc_food_chart.TestFoods
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.AreaOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodFilterTest {
    private val foods = TestFoods.all
    private val none = emptyMap<String, FoodUserRecordEntity>()

    @Test
    fun emptyQueryReturnsEverything() {
        assertEquals(40, FoodFilter.apply(foods, FoodQuery(), none).size)
    }

    @Test
    fun searchesMenuName() {
        val result = FoodFilter.apply(foods, FoodQuery(text = "フォカッチャ"), none)
        assertEquals(2, result.size)
        assertTrue(result.all { it.menu.contains("フォカッチャ") })
    }

    @Test
    fun searchesRestaurant() {
        val result = FoodFilter.apply(foods, FoodQuery(text = "Half Saints"), none)
        assertEquals(2, result.size)
        assertTrue(result.all { it.restaurant == "Half Saints BAKES" })
    }

    @Test
    fun searchesAuthor() {
        val result = FoodFilter.apply(foods, FoodQuery(text = "伊沢"), none)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.author == "伊沢拓司" })
    }

    @Test
    fun searchIsCaseInsensitiveAndTrimmed() {
        val lower = FoodFilter.apply(foods, FoodQuery(text = "  jack in the donuts "), none)
        val upper = FoodFilter.apply(foods, FoodQuery(text = "JACK IN THE DONUTS"), none)
        assertEquals(upper.map { it.id }, lower.map { it.id })
        assertTrue(lower.isNotEmpty())
    }

    @Test
    fun filtersByArea() {
        val result = FoodFilter.apply(foods, FoodQuery(area = "東京ドームホテル"), none)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.area == "東京ドームホテル" })
    }

    @Test
    fun filtersByCoasterTypePrefix() {
        val result = FoodFilter.apply(foods, FoodQuery(coasterType = "謎"), none)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.coaster.startsWith("謎") })
        assertEquals(foods.count { it.coaster.startsWith("謎") }, result.size)
    }

    @Test
    fun filtersByAuthor() {
        val result = FoodFilter.apply(foods, FoodQuery(author = "ふくらP"), none)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.author == "ふくらP" })
    }

    @Test
    fun excludesClosedWhenIncludeClosedIsFalse() {
        val result = FoodFilter.apply(foods, FoodQuery(includeClosed = false), none)
        assertEquals(33, result.size)
        assertFalse(result.any { it.isClosed })
    }

    @Test
    fun unvisitedOnlyHidesEatenFoods() {
        val eatenId = foods[0].id
        val records = mapOf(eatenId to FoodUserRecordEntity(foodId = eatenId, eaten = true))
        val result = FoodFilter.apply(foods, FoodQuery(unvisitedOnly = true), records)
        assertEquals(39, result.size)
        assertFalse(result.any { it.id == eatenId })
    }

    @Test
    fun areaSortFollowsFixedOrderThenRestaurant() {
        val result = FoodFilter.apply(foods, FoodQuery(sort = SortOrder.AREA), none)
        val areaIndexes = result.map { AreaOrder.indexOf(it.area) }
        assertEquals(areaIndexes.sorted(), areaIndexes)
        assertEquals("ラクーア DELI & DISH", result.first().area)
        assertEquals("Space Travelium TeNQ", result.last().area)
    }

    @Test
    fun priceSorts() {
        val asc = FoodFilter.apply(foods, FoodQuery(sort = SortOrder.PRICE_ASC), none).map { it.price }
        assertEquals(asc.sorted(), asc)
        assertEquals(630, asc.first())
        val desc = FoodFilter.apply(foods, FoodQuery(sort = SortOrder.PRICE_DESC), none).map { it.price }
        assertEquals(desc.sortedDescending(), desc)
        assertEquals(2500, desc.first())
    }

    @Test
    fun activeFilterCountIgnoresTextSortAndUnvisited() {
        assertEquals(0, FoodQuery(text = "x", sort = SortOrder.PRICE_ASC, unvisitedOnly = true).activeFilterCount)
        assertEquals(3, FoodQuery(area = "a", coasterType = "謎", includeClosed = false).activeFilterCount)
        assertEquals(4, FoodQuery(area = "a", coasterType = "謎", author = "b", includeClosed = false).activeFilterCount)
    }

    @Test
    fun clearedFiltersKeepsTextSortAndUnvisited() {
        val q = FoodQuery(text = "x", area = "a", coasterType = "謎", author = "b", includeClosed = false, unvisitedOnly = true, sort = SortOrder.PRICE_DESC)
        val cleared = q.clearedFilters()
        assertEquals(FoodQuery(text = "x", unvisitedOnly = true, sort = SortOrder.PRICE_DESC), cleared)
    }

    @Test
    fun sortOrderCycles() {
        assertEquals(SortOrder.PRICE_ASC, SortOrder.AREA.next())
        assertEquals(SortOrder.PRICE_DESC, SortOrder.PRICE_ASC.next())
        assertEquals(SortOrder.AREA, SortOrder.PRICE_DESC.next())
    }
}
