package com.broco.tdc_food_chart.data.model

/**
 * 料理マスターデータ 1 件。`filtered_data.json` の `data[]` 要素に対応する。
 * フィールド名は元 JSON に合わせている（is_closed → isClosed のみ Kotlin 命名）。
 *
 * [id] は JSON には存在しない。店舗名＋メニュー名から決定的に導出した安定 ID で、
 * ユーザーローカルデータ（お気に入り・食べた・メモ・写真）はこの ID に紐づく。
 * 詳細は [FoodIds]。
 */
data class Food(
    val id: String,
    val menu: String,
    val restaurant: String,
    val area: String,
    val author: String,
    val price: Int,
    val coaster: String,
    val url: String,
    val isClosed: Boolean,
) {
    /** コースター種別（"ボイスA" → "ボイス"）。 */
    val coasterType: String get() = coaster.dropLastWhile { it == 'A' || it == 'B' }
}

/** エリアの固定表示順（データ順ではない）。ハンドオフ README の指定どおり。 */
object AreaOrder {
    val ORDER: List<String> = listOf(
        "ラクーア DELI & DISH",
        "ラクーア",
        "スパ ラクーア",
        "東京ドームシティ アトラクションズ",
        "東京ドームホテル",
        "FOOD STADIUM TOKYO",
        "Space Travelium TeNQ",
    )

    /** 未知のエリアは末尾に回す。 */
    fun indexOf(area: String): Int = ORDER.indexOf(area).let { if (it < 0) ORDER.size else it }
}

/** コースター種別の固定順。 */
object CoasterTypes {
    val ORDER: List<String> = listOf("ボイス", "謎", "クイズ")
}
