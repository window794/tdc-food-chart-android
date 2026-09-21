package com.broco.tdc_food_chart.ui.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.ui.components.DetailTopBar
import com.broco.tdc_food_chart.ui.components.MOTION_MS
import com.broco.tdc_food_chart.ui.components.PillButton
import com.broco.tdc_food_chart.ui.components.StarField
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * 「使い方 / ヘルプ」。設定タブから開く。内容は [HelpContent]（アプリ内・オフライン）。
 * セクションはアコーディオン。開いたセクションの末尾に、関連画面への導線（実装済みのものだけ）を置く。
 */
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onAction: (HelpAction) -> Unit,
) {
    // 開いているセクション id。回転しても保つ
    var expanded by rememberSaveable { mutableStateOf(listOf<String>()) }

    Scaffold(containerColor = TdcColors.Navy) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            StarField(modifier = Modifier.fillMaxSize(), alpha = 0.10f)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                DetailTopBar(label = "使い方 / ヘルプ", onBack = onBack)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
                ) {
                    Text(
                        "How to use",
                        style = TdcType.SectionLabel,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    Text(
                        "項目を押すと説明が開きます。",
                        style = TdcType.Caption,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                    HorizontalDivider(thickness = 1.dp, color = TdcColors.Brass22)
                    HelpContent.sections.forEach { section ->
                        val open = section.id in expanded
                        HelpSectionRow(
                            section = section,
                            expanded = open,
                            onToggle = { expanded = if (open) expanded - section.id else expanded + section.id },
                            onAction = onAction,
                        )
                        HorizontalDivider(thickness = 1.dp, color = TdcColors.Brass16)
                    }
                    Text(
                        "この画面の内容はアプリ内に入っているので、電波のない場所でも読めます。",
                        style = TdcType.Caption,
                        modifier = Modifier.padding(top = 18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpSectionRow(
    section: HelpSection,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAction: (HelpAction) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(role = Role.Button, onClick = onToggle)
                .semantics { contentDescription = "${section.title}（${if (expanded) "開いています" else "閉じています"}）" }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    section.title,
                    style = TdcType.Body.copy(fontSize = 14.sp, color = if (expanded) TdcColors.ChampagneStrong else TdcColors.TextMid),
                    modifier = Modifier.semantics { heading() },
                )
                Text(section.english, style = TdcType.SectionLabel.copy(fontSize = 10.sp))
            }
            Chevron(expanded = expanded)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(MOTION_MS)) + fadeIn(tween(MOTION_MS)),
            exit = shrinkVertically(tween(MOTION_MS)) + fadeOut(tween(MOTION_MS)),
        ) {
            Column(
                modifier = Modifier.padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                section.items.forEach { item ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                        // 箇条書きの点（真鍮）。装飾なので読み上げない
                        Box(
                            Modifier
                                .padding(top = 9.dp)
                                .size(4.dp)
                                .clearAndSetSemantics { },
                        ) {
                            Canvas(Modifier.size(4.dp)) { drawCircle(TdcColors.Brass) }
                        }
                        Text(item, style = TdcType.Body.copy(fontSize = 12.5.sp, lineHeight = 21.sp, color = TdcColors.TextMid2), modifier = Modifier.weight(1f))
                    }
                }
                section.action?.let { action ->
                    PillButton(
                        label = "${action.label}　→",
                        onClick = { onAction(action) },
                        modifier = Modifier.padding(top = 6.dp),
                        minHeight = 44.dp,
                    )
                }
            }
        }
    }
}

/** 開閉を示す小さな山形（▼／▲相当）。回転で状態を示す。 */
@Composable
private fun Chevron(expanded: Boolean) {
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, tween(MOTION_MS), label = "chevron")
    Canvas(
        Modifier
            .width(14.dp)
            .size(14.dp)
            .graphicsLayer { rotationZ = rotation }
            .clearAndSetSemantics { },
    ) {
        val w = 1.2.dp.toPx()
        val c = TdcColors.ChampagnePale
        drawLine(c, Offset(size.width * 0.2f, size.height * 0.38f), Offset(size.width * 0.5f, size.height * 0.66f), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(c, Offset(size.width * 0.5f, size.height * 0.66f), Offset(size.width * 0.8f, size.height * 0.38f), strokeWidth = w, cap = StrokeCap.Round)
    }
}
