package com.broco.tdc_food_chart.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodIdsTest {
    @Test
    fun sameInputGivesSameId() {
        assertEquals(FoodIds.of("店", "メニュー"), FoodIds.of("店", "メニュー"))
    }

    @Test
    fun idIsSixteenHexChars() {
        val id = FoodIds.of("JACK IN THE DONUTS", "飛び出せ！いちごのJAM IN THE DONUTS")
        assertEquals(16, id.length)
        assertTrue(id.all { it in "0123456789abcdef" })
    }

    @Test
    fun differentRestaurantOrMenuGivesDifferentId() {
        assertNotEquals(FoodIds.of("A", "m"), FoodIds.of("B", "m"))
        assertNotEquals(FoodIds.of("A", "m1"), FoodIds.of("A", "m2"))
        // 区切り文字があるので "AB"+"C" と "A"+"BC" は衝突しない
        assertNotEquals(FoodIds.of("AB", "C"), FoodIds.of("A", "BC"))
    }
}
