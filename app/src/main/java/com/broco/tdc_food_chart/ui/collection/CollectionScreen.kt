package com.broco.tdc_food_chart.ui.collection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.ui.components.EmptyState
import com.broco.tdc_food_chart.ui.components.FoodRow
import com.broco.tdc_food_chart.ui.components.MOTION_MS
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * コレクションタブ。上部に 3 分割のセグメント（件数付き）、下に一覧。
 */
@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    state: CollectionUiState,
    listState: LazyListState,
    onOpenFood: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        SegmentControl(
            selected = state.segment,
            countOf = state::countOf,
            onSelect = viewModel::setSegment,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
        )
        HorizontalDivider(thickness = 1.dp, color = TdcColors.HairlineWhite)
        if (state.loading) {
            Box(Modifier.fillMaxSize())
        } else if (state.items.isEmpty()) {
            EmptyState(
                title = "no records",
                note = "このタブにはまだ記録がありません。\n探すタブから ☆ や「食べた」を付けると並びます。",
            )
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                items(state.items, key = { it.id }) { food ->
                    val record = state.records[food.id]
                    FoodRow(
                        food = food,
                        favorite = record?.favorite ?: false,
                        eaten = record?.eaten ?: false,
                        showEatenMark = state.segment != CollectionSegment.EATEN,
                        memoExcerpt = if (state.segment == CollectionSegment.MEMO) record?.memo else null,
                        onOpen = { onOpenFood(food.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(food.id) },
                    )
                    HorizontalDivider(thickness = 1.dp, color = TdcColors.HairlineWhite)
                }
            }
        }
    }
}

/**
 * 3 分割セグメント。1px の真鍮罫線で区切り、選択中は Brass18 の面。
 * M3 の SegmentedButton はビジュアルが異なるため、selectableGroup で同じ操作体系だけ借りる。
 */
@Composable
private fun SegmentControl(
    selected: CollectionSegment,
    countOf: (CollectionSegment) -> Int,
    onSelect: (CollectionSegment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(TdcColors.Brass24)
            .border(1.dp, TdcColors.Brass24, shape)
            .padding(1.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        CollectionSegment.entries.forEach { seg ->
            val on = seg == selected
            val bg by animateColorAsState(if (on) TdcColors.Brass18 else TdcColors.Navy, tween(MOTION_MS), label = "segBg")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .background(bg)
                    .selectable(selected = on, role = Role.Tab, onClick = { onSelect(seg) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${seg.label} ${countOf(seg)}",
                    style = TdcType.Chip,
                    color = if (on) TdcColors.ChampagneStrong else TdcColors.TextWeak3,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
