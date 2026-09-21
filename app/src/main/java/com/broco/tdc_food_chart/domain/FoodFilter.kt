package com.broco.tdc_food_chart.domain

import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.AreaOrder
import com.broco.tdc_food_chart.data.model.Food
import java.text.Collator
import java.util.Locale

/** 並び替え。2a プロトタイプの 3 種。 */
enum class SortOrder(val label: String) {
    AREA("エリア順"),
    PRICE_ASC("安い順"),
    PRICE_DESC("高い順"),
    ;

    fun next(): SortOrder = entries[(ordinal + 1) % entries.size]
}

/** 「探す」画面の検索・絞り込み条件。 */
data class FoodQuery(
    val text: String = "",
    /** null = すべて */
    val area: String? = null,
    /** null = 種別すべて。"ボイス" / "謎" / "クイズ" */
    val coasterType: String? = null,
    /** null = すべて */
    val author: String? = null,
    val includeClosed: Boolean = true,
    val unvisitedOnly: Boolean = false,
    val sort: SortOrder = SortOrder.AREA,
) {
    /** 絞り込みチップに出す「設定中の条件数」。検索文字列・並び替え・未食は数えない。 */
    val activeFilterCount: Int
        get() = listOf(area != null, coasterType != null, author != null, !includeClosed).count { it }

    fun clearedFilters(): FoodQuery = copy(area = null, coasterType = null, author = null, includeClosed = true)
}

/**
 * 検索・絞り込み・並び替えの純粋ロジック。Android に依存しないので JVM 単体テストで検証する。
 */
object FoodFilter {
    private val collator: Collator = Collator.getInstance(Locale.JAPANESE)

    /**
     * 検索対象は menu / restaurant / area / author / coaster の連結文字列に対する部分一致（小文字化）。
     * WEB 版と同じ挙動。
     */
    fun matchesText(food: Food, text: String): Boolean {
        val q = text.trim().lowercase()
        if (q.isEmpty()) return true
        val haystack = (food.menu + food.restaurant + food.area + food.author + food.coaster).lowercase()
        return haystack.contains(q)
    }

    fun apply(
        foods: List<Food>,
        query: FoodQuery,
        records: Map<String, FoodUserRecordEntity>,
    ): List<Food> {
        val filtered = foods.filter { food ->
            if (!query.includeClosed && food.isClosed) return@filter false
            if (!matchesText(food, query.text)) return@filter false
            if (query.area != null && food.area != query.area) return@filter false
            if (query.coasterType != null && !food.coaster.startsWith(query.coasterType)) return@filter false
            if (query.author != null && food.author != query.author) return@filter false
            if (query.unvisitedOnly && records[food.id]?.eaten == true) return@filter false
            true
        }
        return sort(filtered, query.sort)
    }

    fun sort(foods: List<Food>, sort: SortOrder): List<Food> = when (sort) {
        SortOrder.AREA -> foods.sortedWith(areaComparator)
        // 同額は既定順（エリア→店舗→メニュー）で安定させる
        SortOrder.PRICE_ASC -> foods.sortedWith(compareBy<Food> { it.price }.then(areaComparator))
        SortOrder.PRICE_DESC -> foods.sortedWith(compareByDescending<Food> { it.price }.then(areaComparator))
    }

    /** エリア既定順 → 店舗名 → メニュー名。 */
    val areaComparator: Comparator<Food> = Comparator { a, b ->
        val byArea = AreaOrder.indexOf(a.area).compareTo(AreaOrder.indexOf(b.area))
        if (byArea != 0) return@Comparator byArea
        val byShop = collator.compare(a.restaurant, b.restaurant)
        if (byShop != 0) return@Comparator byShop
        collator.compare(a.menu, b.menu)
    }
}
