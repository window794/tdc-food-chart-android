package com.broco.tdc_food_chart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broco.tdc_food_chart.data.repository.FoodRepository
import com.broco.tdc_food_chart.data.settings.AppSettings
import com.broco.tdc_food_chart.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** ブランドイントロの段階。WEB 版の data-phase（intro → transfer → done）に対応する。 */
enum class IntroPhase {
    /** 全画面イントロを再生中。ホームはまだ見せない */
    PLAYING,
    /** イントロがフェードアウトし、下でホームがフェードインしている（クロスフェード） */
    TRANSFER,
    /** 終了。イントロは消えている */
    DONE,
}

/**
 * Activity スコープの状態：設定・オンボーディング済みか・ブランドイントロの段階。
 * イントロは ViewModel が生きている間（＝同じタスクでアプリに戻っただけ）は再生しない。
 */
class AppViewModel(
    private val settingsRepository: SettingsRepository,
    private val foodRepository: FoodRepository,
) : ViewModel() {

    /** null = DataStore 読み込み中（スプラッシュを保持する）。 */
    val settings: StateFlow<AppSettings?> = settingsRepository.settings
        .map<AppSettings, AppSettings?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _introPhase = MutableStateFlow(IntroPhase.PLAYING)
    val introPhase: StateFlow<IntroPhase> = _introPhase.asStateFlow()

    /** 料理の総数（設定シート・オンボーディングの文言用）。 */
    val foodCount: StateFlow<Int> = kotlinx.coroutines.flow.flow { emit(foodRepository.getAll().size) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** イントロのクロスフェード開始（ホームを描き始める）。 */
    fun startIntroTransfer() {
        if (_introPhase.value == IntroPhase.PLAYING) _introPhase.value = IntroPhase.TRANSFER
    }

    fun finishIntro() {
        _introPhase.value = IntroPhase.DONE
    }

    /** 設定タブの「ブランドイントロを再生」。起動時とまったく同じイントロを頭から流す。 */
    fun replayIntro() {
        _introPhase.value = IntroPhase.PLAYING
    }

    fun setShowClosed(value: Boolean) {
        viewModelScope.launch { settingsRepository.setShowClosed(value) }
    }

    fun finishOnboarding() {
        viewModelScope.launch { settingsRepository.setOnboardingDone(true) }
    }
}
