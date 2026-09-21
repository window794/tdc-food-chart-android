package com.broco.tdc_food_chart.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodUserRecordDao {
    @Query("SELECT * FROM food_user_record")
    fun observeAll(): Flow<List<FoodUserRecordEntity>>

    @Query("SELECT * FROM food_user_record WHERE food_id = :foodId")
    fun observe(foodId: String): Flow<FoodUserRecordEntity?>

    @Query("SELECT * FROM food_user_record WHERE food_id = :foodId")
    suspend fun get(foodId: String): FoodUserRecordEntity?

    @Upsert
    suspend fun upsert(record: FoodUserRecordEntity)

    @Query("SELECT COUNT(*) FROM food_user_record WHERE eaten = 1")
    suspend fun countEaten(): Int
}

@Dao
interface FoodPhotoDao {
    @Query("SELECT * FROM food_photo WHERE food_id = :foodId ORDER BY sort_order ASC, created_at ASC, id ASC")
    fun observeForFood(foodId: String): Flow<List<FoodPhotoEntity>>

    @Query("SELECT * FROM food_photo WHERE food_id = :foodId ORDER BY sort_order ASC, created_at ASC, id ASC")
    suspend fun getForFood(foodId: String): List<FoodPhotoEntity>

    @Query("SELECT * FROM food_photo WHERE id = :id")
    suspend fun get(id: Long): FoodPhotoEntity?

    @Query("SELECT food_id, COUNT(*) AS count FROM food_photo GROUP BY food_id")
    fun observePhotoCounts(): Flow<List<PhotoCount>>

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM food_photo WHERE food_id = :foodId")
    suspend fun maxSortOrder(foodId: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(photo: FoodPhotoEntity): Long

    @Delete
    suspend fun delete(photo: FoodPhotoEntity)
}

/** 料理ごとの写真枚数（観測記録一覧などでの将来拡張用）。 */
data class PhotoCount(
    @androidx.room.ColumnInfo(name = "food_id") val foodId: String,
    val count: Int,
)
