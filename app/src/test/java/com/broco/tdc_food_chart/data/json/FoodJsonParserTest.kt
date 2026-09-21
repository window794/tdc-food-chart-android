package com.broco.tdc_food_chart.data.json

import com.broco.tdc_food_chart.TestFoods
import com.broco.tdc_food_chart.data.model.AreaOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 同梱の実データ（filtered_data.json）が想定どおり読めることを確認する。 */
class FoodJsonParserTest {

    @Test
    fun parsesAllFortyItems() {
        assertEquals(40, TestFoods.all.size)
    }

    @Test
    fun closedFlagComesFromIsClosed() {
        val foods = TestFoods.all
        assertEquals(7, foods.count { it.isClosed })
        val half = foods.first { it.restaurant == "Half Saints BAKES" }
        assertTrue(half.isClosed)
    }

    @Test
    fun fieldsAreMappedFromRealData() {
        val donut = TestFoods.all.first { it.restaurant == "JACK IN THE DONUTS" }
        assertEquals("飛び出せ！いちごのJAM IN THE DONUTS", donut.menu)
        assertEquals("ラクーア DELI & DISH", donut.area)
        assertEquals("東問", donut.author)
        assertEquals(700, donut.price)
        assertEquals("ボイスB", donut.coaster)
        assertEquals("https://www.laqua.jp/shops/list/jackinthedounuts/", donut.url)
    }

    @Test
    fun idsAreUniqueAndStable() {
        val foods = TestFoods.all
        assertEquals(foods.size, foods.map { it.id }.toSet().size)
        val again = FoodJsonParser.parse(TestFoods.rawJson)
        assertEquals(foods.map { it.id }, again.map { it.id })
    }

    @Test
    fun allAreasAreKnownToAreaOrder() {
        val areas = TestFoods.all.map { it.area }.toSet()
        assertEquals(7, areas.size)
        assertTrue(areas.all { it in AreaOrder.ORDER })
    }

    @Test
    fun priceRangeMatchesHandoff() {
        val prices = TestFoods.all.map { it.price }
        assertEquals(630, prices.min())
        assertEquals(2500, prices.max())
    }

    @Test
    fun coasterTypeStripsVariant() {
        val types = TestFoods.all.map { it.coasterType }.toSet()
        assertEquals(setOf("ボイス", "謎", "クイズ"), types)
    }

    @Test
    fun unknownKeysAreIgnored() {
        val json = """{"summary":{"total":1},"data":[{"area":"a","menu":"m","author":"x","restaurant":"r","url":"u","coaster":"謎A","price":1,"is_closed":false,"extra":1}]}"""
        val foods = FoodJsonParser.parse(json)
        assertEquals(1, foods.size)
        assertEquals("謎A", foods[0].coaster)
    }
}
