package com.broco.tdc_food_chart.ui.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broco.tdc_food_chart.data.local.FoodUserRecordEntity
import com.broco.tdc_food_chart.data.model.Food
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.repository.UserRecordRepository
import com.broco.tdc_food_chart.domain.FoodFilter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** コレクションのセグメント。 */
enum class CollectionSegment(val label: String) {
    FAVORITE("お気に入り"),
    EATEN("食べた"),
    MEMO("メモ"),
}

data class CollectionUiState(
    val loading: Boolean = true,
    val segment: CollectionSegment = CollectionSegment.FAVORITE,
    val favoriteCount: Int = 0,
    val eatenCount: Int = 0,
    val memoCount: Int = 0,
    val items: List<Food> = emptyList(),
    val records: Map<String, FoodUserRecordEntity> = emptyMap(),
) {
    fun countOf(segment: CollectionSegment): Int = when (segment) {
        CollectionSegment.FAVORITE -> favoriteCount
        CollectionSegment.EATEN -> eatenCount
        CollectionSegment.MEMO -> memoCount
    }
}

/**
 * コレクションタブ。お気に入り／食べた／メモを 1 タブ内で切り替える。
 * ユーザー自身の記録なので閉店設定に関係なく全件出す（閉店バッジは付く）。
 */
class CollectionViewModel(
    private val foodRepository: FoodRepository,
    private val userRecordRepository: UserRecordRepository,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    private val segment = savedState.getStateFlow(KEY_SEGMENT, CollectionSegment.FAVORITE.name)
    private val foods = flow { emit(foodRepository.getAll()) }

    val uiState: StateFlow<CollectionUiState> = combine(foods, segment, userRecordRepository.records) { all, seg, records ->
        val current = CollectionSegment.valueOf(seg)
        val favorites = all.filter { records[it.id]?.favorite == true }
        val eaten = all.filter { records[it.id]?.eaten == true }
        val memos = all.filter { records[it.id]?.hasMemo == true }
        val items = when (current) {
            CollectionSegment.FAVORITE -> favorites
            CollectionSegment.EATEN -> eaten
            CollectionSegment.MEMO -> memos
        }
        CollectionUiState(
            loading = false,
            segment = current,
            favoriteCount = favorites.size,
            eatenCount = eaten.size,
            memoCount = memos.size,
            items = FoodFilter.sort(items, com.broco.tdc_food_chart.domain.SortOrder.AREA),
            records = records,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectionUiState())

    fun setSegment(value: CollectionSegment) = savedState.set(KEY_SEGMENT, value.name)

    fun toggleFavorite(foodId: String) {
        viewModelScope.launch { userRecordRepository.toggleFavorite(foodId) }
    }

    private companion object {
        const val KEY_SEGMENT = "segment"
    }
}
