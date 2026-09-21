package com.broco.tdc_food_chart.data.repository

import android.content.Context
import com.broco.tdc_food_chart.data.json.FoodJsonParser
import com.broco.tdc_food_chart.data.model.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * 料理マスターデータ。assets の `filtered_data.json` を一度だけ読み込みメモリに保持する。
 * 読み取り専用。ユーザー記録は [UserRecordRepository] が持つ。
 */
class FoodRepository(context: Context) {
    private val appContext = context.applicationContext
    private val mutex = Mutex()
    @Volatile
    private var cache: List<Food>? = null

    suspend fun getAll(): List<Food> {
        cache?.let { return it }
        return mutex.withLock {
            cache ?: load().also { cache = it }
        }
    }

    suspend fun getById(id: String): Food? = getAll().firstOrNull { it.id == id }

    private suspend fun load(): List<Food> = withContext(Dispatchers.IO) {
        val text = appContext.assets.open(ASSET_NAME).bufferedReader(Charsets.UTF_8).use { it.readText() }
        FoodJsonParser.parse(text)
    }

    companion object {
        const val ASSET_NAME = "filtered_data.json"
    }
}
