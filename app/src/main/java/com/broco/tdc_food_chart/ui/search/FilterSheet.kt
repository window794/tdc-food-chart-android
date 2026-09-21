package com.broco.tdc_food_chart.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.data.model.CoasterTypes
import com.broco.tdc_food_chart.ui.components.CheckIcon
import com.broco.tdc_food_chart.ui.components.PillButton
import com.broco.tdc_food_chart.ui.components.SectionLabel
import com.broco.tdc_food_chart.ui.components.TdcChip
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType
import kotlinx.coroutines.launch

/**
 * 絞り込みボトムシート。エリア／コースター／考案者のチップ、閉店を含めるチェック、
 * 「条件をクリア」「N 件を表示」。
 * 「閉店した店舗のフードも含める」は設定の「閉店したフードを表示」と同じ値（DataStore）を読み書きする。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    state: SearchUiState,
    onDismiss: () -> Unit,
    onSetArea: (String?) -> Unit,
    onSetCoasterType: (String?) -> Unit,
    onSetAuthor: (String?) -> Unit,
    onSetIncludeClosed: (Boolean) -> Unit,
    onClear: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val close: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TdcColors.Sheet,
        contentColor = TdcColors.TextStrong,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = { SheetHandle() },
        scrimColor = TdcColors.Overlay,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 26.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SectionLabel("Filter ／ 絞り込み")

            ChipGroup(label = "エリア") {
                TdcChip("すべて", selected = state.query.area == null, onClick = { onSetArea(null) }, role = Role.RadioButton)
                state.areas.forEach { area ->
                    TdcChip(area, selected = state.query.area == area, onClick = { onSetArea(area) }, role = Role.RadioButton)
                }
            }

            ChipGroup(label = "コースター") {
                TdcChip("種別すべて", selected = state.query.coasterType == null, onClick = { onSetCoasterType(null) }, role = Role.RadioButton)
                CoasterTypes.ORDER.forEach { type ->
                    TdcChip(type, selected = state.query.coasterType == type, onClick = { onSetCoasterType(type) }, role = Role.RadioButton)
                }
            }

            ChipGroup(label = "考案者") {
                TdcChip("すべて", selected = state.query.author == null, onClick = { onSetAuthor(null) }, role = Role.RadioButton)
                state.authors.forEach { author ->
                    TdcChip(author, selected = state.query.author == author, onClick = { onSetAuthor(author) }, role = Role.RadioButton)
                }
            }

            ClosedCheckbox(checked = state.query.includeClosed, onCheckedChange = onSetIncludeClosed)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PillButton("条件をクリア", onClick = onClear, modifier = Modifier.weight(1f))
                PillButton("${state.results.size} 件を表示", onClick = close, emphasized = true, modifier = Modifier.weight(1f))
            }
        }
    }
}

/** シートのドラッグハンドル（34×3、真鍮 40%）。 */
@Composable
fun SheetHandle() {
    Box(
        modifier = Modifier
            .padding(top = 14.dp, bottom = 4.dp)
            .size(width = 34.dp, height = 3.dp)
            .background(TdcColors.Brass40, RoundedCornerShape(999.dp)),
    )
}

@Composable
private fun ChipGroup(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, style = TdcType.DefLabel.copy(color = TdcColors.TextWeak2))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            content()
        }
    }
}

/** 15px の四角、ON で真鍮塗り。行全体（44dp）がタップ領域。 */
@Composable
private fun ClosedCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .background(if (checked) TdcColors.Brass else Color.Transparent, RoundedCornerShape(2.dp))
                .border(1.dp, if (checked) TdcColors.Brass.copy(alpha = 0.8f) else TdcColors.Brass34, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) CheckIcon(color = TdcColors.Navy, size = 10.dp)
        }
        Text(
            "閉店した店舗のフードも含める",
            style = TdcType.Body,
            color = if (checked) TdcColors.TextMid3 else TdcColors.TextWeak,
        )
    }
}
