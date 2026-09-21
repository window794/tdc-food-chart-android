package com.broco.tdc_food_chart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * ユーザーローカルデータ専用の Room DB。料理マスターは含めない（JSON から毎回読む）。
 * `exportSchema = true` で app/schemas に版ごとのスキーマを残し、将来のマイグレーションに備える。
 */
@Database(
    entities = [FoodUserRecordEntity::class, FoodPhotoEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodUserRecordDao(): FoodUserRecordDao
    abstract fun foodPhotoDao(): FoodPhotoDao

    companion object {
        const val NAME = "tdc_food_chart.db"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME)
                .build()
    }
}
