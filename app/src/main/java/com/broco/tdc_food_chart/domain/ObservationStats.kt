package com.broco.tdc_food_chart.domain

import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.AreaOrder
import com.broco.tdc_food_chart.data.model.Food
import java.time.LocalDate
import kotlin.math.roundToInt

/** エリア別の達成状況。 */
data class AreaProgress(
    val area: String,
    val eaten: Int,
    val total: Int,
) {
    val fraction: Float get() = if (total == 0) 0f else eaten.toFloat() / total
}

/** 最近の観測記録 1 件。 */
data class RecentObservation(
    val food: Food,
    /** 食べた日。未設定の記録は日付なしで末尾に並ぶ。 */
    val eatenDate: LocalDate?,
)

/** 観測記録タブに出す数値一式。 */
data class ObservationSummary(
    val eatenCount: Int,
    val totalCount: Int,
    val areaProgress: List<AreaProgress>,
    val recent: List<RecentObservation>,
) {
    val remaining: Int get() = totalCount - eatenCount
    val fraction: Float get() = if (totalCount == 0) 0f else eatenCount.toFloat() / totalCount
    /** 達成率（％、四捨五入）。 */
    val percent: Int get() = (fraction * 100).roundToInt()
}

/** 達成率・エリア別進捗・最近の記録を計算する純粋ロジック。 */
object ObservationStats {
    fun summarize(
        foods: List<Food>,
        records: Map<String, FoodUserRecordEntity>,
        recentLimit: Int = 5,
    ): ObservationSummary {
        val eatenFoods = foods.filter { records[it.id]?.eaten == true }

        // エリアは固定順。マスターに存在するエリアだけを出し、未知のエリアは末尾に追加する
        val areas = AreaOrder.ORDER.filter { area -> foods.any { it.area == area } } +
            foods.map { it.area }.distinct().filter { it !in AreaOrder.ORDER }
        val areaProgress = areas.map { area ->
            val all = foods.filter { it.area == area }
            AreaProgress(area = area, eaten = all.count { records[it.id]?.eaten == true }, total = all.size)
        }

        // eatenDate の新しい順。日付なしは後ろ、同日の中では更新日時の新しい順
        val recent = eatenFoods
            .map { RecentObservation(it, records[it.id]?.eatenDate) }
            .sortedWith(
                compareByDescending<RecentObservation> { it.eatenDate != null }
                    .thenByDescending { it.eatenDate }
                    .thenByDescending { records[it.food.id]?.updatedAt }
            )
            .take(recentLimit)

        return ObservationSummary(
            eatenCount = eatenFoods.size,
            totalCount = foods.size,
            areaProgress = areaProgress,
            recent = recent,
        )
    }
}
