package com.broco.tdc_food_chart.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.AreaOrder
import com.broco.tdc_food_chart.data.model.Food
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.repository.UserRecordRepository
import com.broco.tdc_food_chart.data.settings.SettingsRepository
import com.broco.tdc_food_chart.domain.FoodFilter
import com.broco.tdc_food_chart.domain.FoodQuery
import com.broco.tdc_food_chart.domain.SortOrder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

/** 「探す」画面の状態。 */
data class SearchUiState(
    val loading: Boolean = true,
    val query: FoodQuery = FoodQuery(),
    val results: List<Food> = emptyList(),
    val records: Map<String, FoodUserRecordEntity> = emptyMap(),
    /** 絞り込みシートのチップ用。マスターに存在するエリアのみ、固定順。 */
    val areas: List<String> = emptyList(),
    val authors: List<String> = emptyList(),
)

/**
 * 検索文字列・絞り込み・並び替えは SavedStateHandle に持ち、タブを行き来しても初期化されない。
 * 「閉店を含める」だけは設定（DataStore）と共有する。
 */
class SearchViewModel(
    private val foodRepository: FoodRepository,
    private val userRecordRepository: UserRecordRepository,
    private val settingsRepository: SettingsRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    private val text = savedState.getStateFlow(KEY_TEXT, "")
    private val area = savedState.getStateFlow<String?>(KEY_AREA, null)
    private val coasterType = savedState.getStateFlow<String?>(KEY_COASTER, null)
    private val author = savedState.getStateFlow<String?>(KEY_AUTHOR, null)
    private val unvisitedOnly = savedState.getStateFlow(KEY_UNVISITED, false)
    private val sort = savedState.getStateFlow(KEY_SORT, SortOrder.AREA.name)

    private val foods = flow { emit(foodRepository.getAll()) }

    private val query = combine(text, area, coasterType, author, unvisitedOnly, sort, settingsRepository.settings) { values ->
        @Suppress("UNCHECKED_CAST")
        FoodQuery(
            text = values[0] as String,
            area = values[1] as String?,
            coasterType = values[2] as String?,
            author = values[3] as String?,
            unvisitedOnly = values[4] as Boolean,
            sort = SortOrder.valueOf(values[5] as String),
            includeClosed = (values[6] as com.broco.tdc_food_chart.data.settings.AppSettings).showClosed,
        )
    }

    val uiState: StateFlow<SearchUiState> = combine(foods, query, userRecordRepository.records) { all, q, records ->
        val collator = Collator.getInstance(Locale.JAPANESE)
        SearchUiState(
            loading = false,
            query = q,
            results = FoodFilter.apply(all, q, records),
            records = records,
            areas = AreaOrder.ORDER.filter { area -> all.any { it.area == area } } +
                all.map { it.area }.distinct().filter { it !in AreaOrder.ORDER },
            authors = all.map { it.author }.distinct().sortedWith { a, b -> collator.compare(a, b) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    fun setText(value: String) = savedState.set(KEY_TEXT, value)
    fun clearText() = savedState.set(KEY_TEXT, "")
    fun setArea(value: String?) = savedState.set(KEY_AREA, value)
    fun setCoasterType(value: String?) = savedState.set(KEY_COASTER, value)
    fun setAuthor(value: String?) = savedState.set(KEY_AUTHOR, value)
    fun toggleUnvisitedOnly() = savedState.set(KEY_UNVISITED, !(savedState.get<Boolean>(KEY_UNVISITED) ?: false))
    fun setSort(value: SortOrder) = savedState.set(KEY_SORT, value.name)

    fun setIncludeClosed(value: Boolean) {
        viewModelScope.launch { settingsRepository.setShowClosed(value) }
    }

    /** 「条件をクリア」：エリア・コースター・考案者・閉店を初期化。検索文字列・並び替え・未食は維持。 */
    fun clearFilters() {
        savedState[KEY_AREA] = null
        savedState[KEY_COASTER] = null
        savedState[KEY_AUTHOR] = null
        setIncludeClosed(true)
    }

    fun toggleFavorite(foodId: String) {
        viewModelScope.launch { userRecordRepository.toggleFavorite(foodId) }
    }

    private companion object {
        const val KEY_TEXT = "text"
        const val KEY_AREA = "area"
        const val KEY_COASTER = "coaster"
        const val KEY_AUTHOR = "author"
        const val KEY_UNVISITED = "unvisited"
        const val KEY_SORT = "sort"
    }
}
