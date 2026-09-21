package com.broco.tdc_food_chart.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Room DAO の実機／エミュレータテスト（in-memory DB）。
 * eatenDate（LocalDate）・capturedAt（LocalDateTime）が TypeConverter を通して往復することを確認する。
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var records: FoodUserRecordDao
    private lateinit var photos: FoodPhotoDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        records = db.foodUserRecordDao()
        photos = db.foodPhotoDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun userRecordRoundTrip() = runTest {
        val entity = FoodUserRecordEntity(
            foodId = "f1",
            favorite = true,
            eaten = true,
            eatenDate = LocalDate.of(2026, 9, 20),
            memo = "メモ",
            updatedAt = Instant.ofEpochMilli(123),
        )
        records.upsert(entity)
        assertEquals(entity, records.get("f1"))
        assertEquals(entity, records.observe("f1").first())
        assertEquals(1, records.countEaten())

        // upsert で上書き
        records.upsert(entity.copy(eaten = false))
        assertEquals(false, records.get("f1")!!.eaten)
        assertEquals(LocalDate.of(2026, 9, 20), records.get("f1")!!.eatenDate)
        assertEquals(0, records.countEaten())
    }

    @Test
    fun photoRoundTripAndOrdering() = runTest {
        val p1 = FoodPhotoEntity(
            foodId = "f1", localPath = "food_photos/f1/a.jpg", mimeType = "image/jpeg",
            createdAt = Instant.ofEpochMilli(1), capturedAt = LocalDateTime.of(2026, 9, 20, 12, 0), sortOrder = 0,
        )
        val p2 = FoodPhotoEntity(
            foodId = "f1", localPath = "food_photos/f1/b.png", mimeType = "image/png",
            createdAt = Instant.ofEpochMilli(2), capturedAt = null, sortOrder = 1,
        )
        val other = FoodPhotoEntity(
            foodId = "f2", localPath = "food_photos/f2/c.jpg", mimeType = "image/jpeg",
            createdAt = Instant.ofEpochMilli(3), capturedAt = null, sortOrder = 0,
        )
        val id1 = photos.insert(p1)
        val id2 = photos.insert(p2)
        photos.insert(other)

        val list = photos.getForFood("f1")
        assertEquals(listOf(id1, id2), list.map { it.id })
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 0), list[0].capturedAt)
        assertNull(list[1].capturedAt)
        assertEquals(1, photos.maxSortOrder("f1"))
        assertEquals(-1, photos.maxSortOrder("none"))

        val counts = photos.observePhotoCounts().first().associate { it.foodId to it.count }
        assertEquals(mapOf("f1" to 2, "f2" to 1), counts)

        photos.delete(list[0])
        assertEquals(listOf(id2), photos.getForFood("f1").map { it.id })
        assertTrue(photos.get(id1) == null)
    }
}
