package com.broco.tdc_food_chart.ui.observation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.repository.UserRecordRepository
import com.broco.tdc_food_chart.domain.ObservationStats
import com.broco.tdc_food_chart.domain.ObservationSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

/** 観測記録タブ。食べた状態・食べた日が変わると即座に再計算される。 */
class ObservationViewModel(
    foodRepository: FoodRepository,
    userRecordRepository: UserRecordRepository,
) : ViewModel() {

    private val foods = flow { emit(foodRepository.getAll()) }

    /** null = 読み込み中。 */
    val summary: StateFlow<ObservationSummary?> = combine(foods, userRecordRepository.records) { all, records ->
        ObservationStats.summarize(all, records, recentLimit = RECENT_LIMIT)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private companion object {
        const val RECENT_LIMIT = 5
    }
}
