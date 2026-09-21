package com.broco.tdc_food_chart

import com.broco.tdc_food_chart.data.json.FoodJsonParser
import com.broco.tdc_food_chart.data.model.Food
import java.io.File

/**
 * テスト用の料理データ。架空データは作らず、同梱している実データ（assets/filtered_data.json）を読む。
 * 単体テストの作業ディレクトリは app/ モジュール直下。
 */
object TestFoods {
    private val assetFile: File
        get() = listOf(
            File("src/main/assets/filtered_data.json"),
            File("app/src/main/assets/filtered_data.json"),
        ).first { it.exists() }

    val all: List<Food> by lazy { FoodJsonParser.parse(assetFile.readText(Charsets.UTF_8)) }

    val rawJson: String by lazy { assetFile.readText(Charsets.UTF_8) }
}
