package com.broco.tdc_food_chart.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.broco.tdc_food_chart.ui.AppViewModel
import com.broco.tdc_food_chart.ui.appViewModel
import com.broco.tdc_food_chart.ui.collection.CollectionScreen
import com.broco.tdc_food_chart.ui.collection.CollectionSegment
import com.broco.tdc_food_chart.ui.collection.CollectionViewModel
import com.broco.tdc_food_chart.ui.components.LogIcon
import com.broco.tdc_food_chart.ui.components.MOTION_MS
import com.broco.tdc_food_chart.ui.components.SearchIcon
import com.broco.tdc_food_chart.ui.components.SettingsIcon
import com.broco.tdc_food_chart.ui.components.StarField
import com.broco.tdc_food_chart.ui.components.StarIcon
import com.broco.tdc_food_chart.ui.components.TabTopBar
import com.broco.tdc_food_chart.ui.navigation.KEY_REQUEST_SEGMENT
import com.broco.tdc_food_chart.ui.navigation.KEY_REQUEST_TAB
import com.broco.tdc_food_chart.ui.observation.ObservationScreen
import com.broco.tdc_food_chart.ui.observation.ObservationViewModel
import com.broco.tdc_food_chart.ui.search.SearchScreen
import com.broco.tdc_food_chart.ui.search.SearchViewModel
import com.broco.tdc_food_chart.ui.settings.SettingsScreen
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/** ボトムナビの 4 タブ。設定は右上メニューから 4 つ目のタブに移した（項目が増えたため）。 */
enum class HomeTab(val label: String, val title: String, val subtitle: String) {
    SEARCH("探す", "TDC FOOD CHART", "探る昼とめぐる夜"),
    COLLECTION("コレクション", "Collection", "お気に入り・食べた・メモ"),
    OBSERVATION("観測記録", "Observation log", "制覇率と観測記録"),
    SETTINGS("設定", "Settings", "使い方・表示・このアプリについて"),
}

/**
 * ホーム。ヘッダー＋タブ内容＋ボトムナビ。
 * 各タブの ViewModel はこの画面（Home の NavBackStackEntry）に紐づくので、タブを切り替えても
 * 検索文字列・絞り込み・セグメントは消えない。スクロール位置は SaveableStateHolder で保持する。
 */
@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    navRequests: SavedStateHandle,
    onOpenFood: (String) -> Unit,
    onOpenHelp: () -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(HomeTab.SEARCH) }
    val stateHolder = rememberSaveableStateHolder()

    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    val foodCount by appViewModel.foodCount.collectAsStateWithLifecycle()

    val searchViewModel = appViewModel<SearchViewModel> { c, handle ->
        SearchViewModel(c.foodRepository, c.userRecordRepository, c.settingsRepository, handle)
    }
    val collectionViewModel = appViewModel<CollectionViewModel> { c, handle ->
        CollectionViewModel(c.foodRepository, c.userRecordRepository, handle)
    }
    val observationViewModel = appViewModel<ObservationViewModel> { c, _ ->
        ObservationViewModel(c.foodRepository, c.userRecordRepository)
    }

    // ヘルプの「○○を見る」から戻ってきたときのタブ／セグメント要求を 1 回だけ消費する
    val requestedTab by navRequests.getStateFlow<String?>(KEY_REQUEST_TAB, null).collectAsStateWithLifecycle()
    LaunchedEffect(requestedTab) {
        val name = requestedTab ?: return@LaunchedEffect
        HomeTab.entries.firstOrNull { it.name == name }?.let { tab = it }
        navRequests.get<String?>(KEY_REQUEST_SEGMENT)?.let { seg ->
            CollectionSegment.entries.firstOrNull { it.name == seg }?.let(collectionViewModel::setSegment)
        }
        navRequests[KEY_REQUEST_TAB] = null
        navRequests[KEY_REQUEST_SEGMENT] = null
    }

    // 探す以外のタブでシステムバック → 探すへ戻る（Android の一般的な作法）
    BackHandler(enabled = tab != HomeTab.SEARCH) { tab = HomeTab.SEARCH }

    Scaffold(
        containerColor = TdcColors.Navy,
        bottomBar = { BottomNav(selected = tab, onSelect = { tab = it }) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            StarField(modifier = Modifier.fillMaxSize(), alpha = 0.10f)
            Column(Modifier.fillMaxSize()) {
                TabTopBar(title = tab.title, subtitle = tab.subtitle)
                stateHolder.SaveableStateProvider(key = tab.name) {
                    when (tab) {
                        HomeTab.SEARCH -> {
                            val state by searchViewModel.uiState.collectAsStateWithLifecycle()
                            val listState = rememberLazyListState()
                            SearchScreen(
                                viewModel = searchViewModel,
                                state = state,
                                listState = listState,
                                onOpenFood = onOpenFood,
                            )
                        }
                        HomeTab.COLLECTION -> {
                            val state by collectionViewModel.uiState.collectAsStateWithLifecycle()
                            val listState: LazyListState = rememberLazyListState()
                            CollectionScreen(
                                viewModel = collectionViewModel,
                                state = state,
                                listState = listState,
                                onOpenFood = onOpenFood,
                            )
                        }
                        HomeTab.OBSERVATION -> {
                            val summary by observationViewModel.summary.collectAsStateWithLifecycle()
                            val scrollState = rememberScrollState()
                            ObservationScreen(summary = summary, scrollState = scrollState, onOpenFood = onOpenFood)
                        }
                        HomeTab.SETTINGS -> {
                            val scrollState = rememberScrollState()
                            SettingsScreen(
                                showClosed = settings?.showClosed ?: true,
                                foodCount = foodCount,
                                scrollState = scrollState,
                                onShowClosedChange = appViewModel::setShowClosed,
                                onReplayIntro = appViewModel::replayIntro,
                                onOpenHelp = onOpenHelp,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ボトムナビ（4 タブ）。M3 の選択インジケータ（52×28 の pill）＋ラベル。背景 #101C2B、上罫線 Brass22。
 * tonal elevation は使わない。各タブは幅 1/4・高さ 60dp 以上でタップ領域を確保する。
 */
@Composable
private fun BottomNav(selected: HomeTab, onSelect: (HomeTab) -> Unit) {
    Column(Modifier.background(TdcColors.Sheet)) {
        HorizontalDivider(thickness = 1.dp, color = TdcColors.Brass22)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 6.dp, bottom = 10.dp)
                .selectableGroup(),
        ) {
            HomeTab.entries.forEach { tab ->
                val on = tab == selected
                val indicator by animateColorAsState(if (on) TdcColors.Brass18 else Color.Transparent, tween(MOTION_MS), label = "tabIndicator")
                val fg by animateColorAsState(if (on) TdcColors.ChampagneStrong else TdcColors.IconMuted, tween(MOTION_MS), label = "tabFg")
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .selectable(selected = on, role = Role.Tab, onClick = { onSelect(tab) })
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 52.dp, height = 28.dp)
                            .background(indicator, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (tab) {
                            HomeTab.SEARCH -> SearchIcon(color = fg, size = 14.dp)
                            HomeTab.COLLECTION -> StarIcon(filled = on, color = fg, size = 14.dp)
                            HomeTab.OBSERVATION -> LogIcon(color = fg, size = 14.dp)
                            HomeTab.SETTINGS -> SettingsIcon(color = fg, size = 14.dp)
                        }
                    }
                    Text(tab.label, style = TdcType.Badge.copy(fontSize = 10.5.sp, letterSpacing = 0.6.sp), color = fg, maxLines = 1)
                }
            }
        }
    }
}
