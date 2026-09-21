package com.broco.tdc_food_chart.data.json

import com.broco.tdc_food_chart.data.model.Food
import com.broco.tdc_food_chart.data.model.FoodIds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** `filtered_data.json` のルート。`summary` / `closed_urls` は表示に使わないため読み飛ばす。 */
@Serializable
private data class FoodJsonRoot(
    val data: List<FoodJsonItem> = emptyList(),
)

/** `filtered_data.json` の 1 要素。フィールド名は元データそのまま。 */
@Serializable
private data class FoodJsonItem(
    val area: String,
    val menu: String,
    val author: String,
    val restaurant: String,
    val url: String,
    val coaster: String,
    val price: Int,
    @SerialName("is_closed") val isClosed: Boolean = false,
)

/** 同梱 JSON を [Food] のリストへ変換する。純粋な Kotlin なので JVM 単体テストで検証できる。 */
object FoodJsonParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): List<Food> {
        val root = json.decodeFromString(FoodJsonRoot.serializer(), text)
        return root.data.map { item ->
            Food(
                id = FoodIds.of(item.restaurant, item.menu),
                menu = item.menu,
                restaurant = item.restaurant,
                area = item.area,
                author = item.author,
                price = item.price,
                coaster = item.coaster,
                url = item.url,
                isClosed = item.isClosed,
            )
        }
    }
}
