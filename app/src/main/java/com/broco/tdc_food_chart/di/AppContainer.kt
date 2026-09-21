package com.broco.tdc_food_chart.di

import android.content.Context
import com.broco.tdc_food_chart.data.local.AppDatabase
import com.broco.tdc_food_chart.data.photo.PhotoStorage
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.repository.PhotoRepository
import com.broco.tdc_food_chart.data.repository.UserRecordRepository
import com.broco.tdc_food_chart.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * 手書きの DI コンテナ。アプリ規模的に Hilt 等は入れず、Application が 1 つ持つ。
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    /** 画面のライフサイクルより長く生きる書き込み用スコープ（メモの書き切りなど）。 */
    val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.build(appContext) }
    val foodRepository: FoodRepository by lazy { FoodRepository(appContext) }
    val userRecordRepository: UserRecordRepository by lazy { UserRecordRepository(database.foodUserRecordDao()) }
    val photoRepository: PhotoRepository by lazy {
        PhotoRepository(database.foodPhotoDao(), PhotoStorage(appContext))
    }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(appContext) }
}
