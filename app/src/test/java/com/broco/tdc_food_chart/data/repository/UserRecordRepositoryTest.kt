package com.broco.tdc_food_chart.data.repository

import com.broco.tdc_food_chart.data.local.FoodUserRecordDao
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Room を使わないメモリ上の DAO。eatenDate / memo の扱いを検証するため。 */
private class FakeUserRecordDao : FoodUserRecordDao {
    val state = MutableStateFlow<Map<String, FoodUserRecordEntity>>(emptyMap())

    override fun observeAll(): Flow<List<FoodUserRecordEntity>> = state.map { it.values.toList() }
    override fun observe(foodId: String): Flow<FoodUserRecordEntity?> = state.map { it[foodId] }
    override suspend fun get(foodId: String): FoodUserRecordEntity? = state.value[foodId]
    override suspend fun upsert(record: FoodUserRecordEntity) {
        state.value = state.value + (record.foodId to record)
    }
    override suspend fun countEaten(): Int = state.value.values.count { it.eaten }
}

class UserRecordRepositoryTest {
    // 2026-09-21 23:30 JST。UTC では 9/21 14:30 だが、「今日」は端末ローカルの 9/21 であること
    private val zone: ZoneId = ZoneId.of("Asia/Tokyo")
    private val clock: Clock = Clock.fixed(Instant.parse("2026-09-21T14:30:00Z"), zone)
    private val dao = FakeUserRecordDao()
    private val repo = UserRecordRepository(dao, clock)
    private val id = "food-1"

    @Test
    fun turningEatenOnSetsTodayWhenDateUnset() = runTest {
        repo.setEaten(id, true)
        val r = dao.get(id)!!
        assertTrue(r.eaten)
        assertEquals(LocalDate.of(2026, 9, 21), r.eatenDate)
    }

    @Test
    fun turningEatenOnKeepsExistingDate() = runTest {
        repo.setEatenDate(id, LocalDate.of(2026, 8, 1))
        repo.setEaten(id, false)
        repo.setEaten(id, true)
        assertEquals(LocalDate.of(2026, 8, 1), dao.get(id)!!.eatenDate)
    }

    @Test
    fun turningEatenOffKeepsDateAndMemo() = runTest {
        repo.setEaten(id, true)
        repo.setMemo(id, "おいしかった")
        repo.setEaten(id, false)
        val r = dao.get(id)!!
        assertFalse(r.eaten)
        assertEquals(LocalDate.of(2026, 9, 21), r.eatenDate)
        assertEquals("おいしかった", r.memo)
    }

    @Test
    fun toggleEatenFlipsState() = runTest {
        repo.toggleEaten(id)
        assertTrue(dao.get(id)!!.eaten)
        repo.toggleEaten(id)
        assertFalse(dao.get(id)!!.eaten)
    }

    @Test
    fun setEatenDateMarksEaten() = runTest {
        repo.setEatenDate(id, LocalDate.of(2026, 7, 7))
        val r = dao.get(id)!!
        assertTrue(r.eaten)
        assertEquals(LocalDate.of(2026, 7, 7), r.eatenDate)
    }

    @Test
    fun adoptCapturedDateOnlyWhenUnset() = runTest {
        repo.adoptCapturedDateIfUnset(id, LocalDate.of(2026, 5, 5))
        assertEquals(LocalDate.of(2026, 5, 5), dao.get(id)!!.eatenDate)
        assertTrue(dao.get(id)!!.eaten)

        // 既に日付があるときは上書きしない
        repo.adoptCapturedDateIfUnset(id, LocalDate.of(2026, 6, 6))
        assertEquals(LocalDate.of(2026, 5, 5), dao.get(id)!!.eatenDate)
    }

    @Test
    fun favoriteToggleDoesNotTouchOtherFields() = runTest {
        repo.setEatenDate(id, LocalDate.of(2026, 1, 1))
        repo.setMemo(id, "m")
        repo.toggleFavorite(id)
        val r = dao.get(id)!!
        assertTrue(r.favorite)
        assertTrue(r.eaten)
        assertEquals(LocalDate.of(2026, 1, 1), r.eatenDate)
        assertEquals("m", r.memo)
        repo.setFavorite(id, false)
        assertFalse(dao.get(id)!!.favorite)
    }

    @Test
    fun recordsFlowIsKeyedByFoodId() = runTest {
        repo.setFavorite("a", true)
        repo.setMemo("b", "x")
        val map = repo.records.first()
        assertEquals(setOf("a", "b"), map.keys)
        assertNull(map["c"])
    }

    @Test
    fun noWriteWhenNothingChanges() = runTest {
        repo.setFavorite(id, false)
        assertNull(dao.get(id))
    }
}
