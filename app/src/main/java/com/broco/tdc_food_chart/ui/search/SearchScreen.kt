package com.broco.tdc_food_chart.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.domain.SortOrder
import com.broco.tdc_food_chart.ui.components.CloseIcon
import com.broco.tdc_food_chart.ui.components.EmptyState
import com.broco.tdc_food_chart.ui.components.FoodRow
import com.broco.tdc_food_chart.ui.components.SearchIcon
import com.broco.tdc_food_chart.ui.components.TdcChip
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * 「探す」タブ。検索バー（pill）＋チップ行（絞り込み／未食のみ／並び替え）＋結果一覧。
 * 絞り込みは [FilterSheet]（ボトムシート）で開く。
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    state: SearchUiState,
    listState: LazyListState,
    onOpenFood: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterOpen by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            text = state.query.text,
            onTextChange = viewModel::setText,
            onClear = viewModel::clearText,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        ChipRow(
            state = state,
            onOpenFilter = { filterOpen = true },
            onToggleUnvisited = viewModel::toggleUnvisitedOnly,
            onSetSort = viewModel::setSort,
            modifier = Modifier.padding(top = 10.dp, bottom = 12.dp),
        )
        HorizontalDivider(thickness = 1.dp, color = TdcColors.HairlineWhite)
        if (state.loading) {
            Box(Modifier.fillMaxSize())
        } else if (state.results.isEmpty()) {
            EmptyState(
                title = "no results",
                note = "条件に合うフードが見つかりませんでした。\nキーワードを短くするか、絞り込みを解除してください。",
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(state.results, key = { it.id }) { food ->
                    val record = state.records[food.id]
                    FoodRow(
                        food = food,
                        favorite = record?.favorite ?: false,
                        eaten = record?.eaten ?: false,
                        onOpen = { onOpenFood(food.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(food.id) },
                    )
                    HorizontalDivider(thickness = 1.dp, color = TdcColors.HairlineWhite)
                }
            }
        }
    }

    if (filterOpen) {
        FilterSheet(
            state = state,
            onDismiss = { filterOpen = false },
            onSetArea = viewModel::setArea,
            onSetCoasterType = viewModel::setCoasterType,
            onSetAuthor = viewModel::setAuthor,
            onSetIncludeClosed = viewModel::setIncludeClosed,
            onClear = viewModel::clearFilters,
        )
    }
}

@Composable
private fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .background(TdcColors.SurfaceFaint2, CircleShape)
            .border(1.dp, TdcColors.Brass34, CircleShape)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SearchIcon(color = TdcColors.Brass, size = 13.dp)
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            textStyle = TdcType.Body.copy(color = TdcColors.TextStrong),
            cursorBrush = SolidColor(TdcColors.Champagne),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "検索" },
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (text.isEmpty()) {
                        Text("メニュー・店舗・考案者で検索", style = TdcType.Body, color = TdcColors.Placeholder, maxLines = 1)
                    }
                    inner()
                }
            },
        )
        if (text.isNotEmpty()) {
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .size(44.dp)
                    .semantics { contentDescription = "検索文字列を消す" },
            ) {
                CloseIcon(color = TdcColors.IconMuted)
            }
        } else {
            Box(Modifier.size(44.dp))
        }
    }
}

@Composable
private fun ChipRow(
    state: SearchUiState,
    onOpenFilter: () -> Unit,
    onToggleUnvisited: () -> Unit,
    onSetSort: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sortMenuOpen by rememberSaveable { mutableStateOf(false) }
    val activeCount = state.query.activeFilterCount
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        TdcChip(
            label = if (activeCount > 0) "絞り込み $activeCount" else "絞り込み",
            selected = activeCount > 0,
            onClick = onOpenFilter,
        )
        TdcChip(
            label = "未食のみ",
            selected = state.query.unvisitedOnly,
            onClick = onToggleUnvisited,
            role = androidx.compose.ui.semantics.Role.Checkbox,
        )
        Box {
            TdcChip(
                label = state.query.sort.label,
                selected = state.query.sort != SortOrder.AREA,
                onClick = { sortMenuOpen = true },
                modifier = Modifier.semantics { contentDescription = "並び替え: ${state.query.sort.label}" },
            )
            DropdownMenu(
                expanded = sortMenuOpen,
                onDismissRequest = { sortMenuOpen = false },
                containerColor = TdcColors.Sheet,
                border = androidx.compose.foundation.BorderStroke(1.dp, TdcColors.Brass30),
            ) {
                SortOrder.entries.forEach { sort ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                sort.label,
                                style = TdcType.Chip,
                                color = if (sort == state.query.sort) TdcColors.ChampagneStrong else TdcColors.TextMid,
                            )
                        },
                        onClick = {
                            onSetSort(sort)
                            sortMenuOpen = false
                        },
                    )
                }
            }
        }
    }
}
