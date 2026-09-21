package com.broco.tdc_food_chart.ui.observation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.domain.ObservationSummary
import com.broco.tdc_food_chart.ui.components.SectionLabel
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 観測記録タブ。Charted（食べた数／全料理数・達成率・残り）→ By area → Latest。
 */
@Composable
fun ObservationScreen(
    summary: ObservationSummary?,
    scrollState: androidx.compose.foundation.ScrollState,
    onOpenFood: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (summary == null) {
        Box(modifier.fillMaxSize())
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ChartedCard(summary)

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SectionLabel("By area")
            summary.areaProgress.forEach { ap ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.semantics { contentDescription = "${ap.area} ${ap.eaten} / ${ap.total}" },
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            ap.area,
                            style = TdcType.RowSub.copy(color = TdcColors.TextMid2, fontSize = TdcType.Body.fontSize),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text("${ap.eaten} / ${ap.total}", style = TdcType.SmallNumber)
                    }
                    ProgressLine(fraction = ap.fraction, height = 1.dp)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionLabel("Latest", modifier = Modifier.padding(bottom = 7.dp))
            if (summary.recent.isEmpty()) {
                Text(
                    "まだ観測記録がありません。\nフード詳細で「食べた」を付けると、ここに並びます。",
                    style = TdcType.Caption,
                )
            } else {
                summary.recent.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp)
                            .clickable(role = Role.Button) { onOpenFood(item.food.id) },
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            item.food.menu,
                            style = TdcType.RowSub.copy(color = TdcColors.TextMid2, fontSize = TdcType.Body.fontSize),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            item.eatenDate?.let { formatShortDate(it) } ?: "—",
                            style = TdcType.SmallNumber.copy(color = TdcColors.TextWeak2),
                        )
                    }
                }
            }
        }

        Text(
            "食べた記録・メモ・写真はこの端末内にだけ保存されます。",
            style = TdcType.Caption,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ChartedCard(summary: ObservationSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TdcColors.SurfaceFaint, RoundedCornerShape(5.dp))
            .border(1.dp, TdcColors.Brass24, RoundedCornerShape(5.dp))
            .padding(horizontal = 20.dp, vertical = 26.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "観測済み ${summary.eatenCount} / ${summary.totalCount}、${summary.percent}%、残り ${summary.remaining} 品"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        SectionLabel("Charted")
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${summary.eatenCount}", style = TdcType.StatNumber)
            Text("/ ${summary.totalCount}", style = TdcType.StatDenominator, modifier = Modifier.padding(bottom = 6.dp))
        }
        ProgressLine(fraction = summary.fraction, height = 2.dp)
        Text(
            "観測済み ${summary.percent}%　残り ${summary.remaining} 品",
            style = TdcType.BodyMuted.copy(fontSize = TdcType.Caption.fontSize),
        )
    }
}

/** 真鍮の細いプログレス線。幅の変化は 0.4s。 */
@Composable
private fun ProgressLine(fraction: Float, height: androidx.compose.ui.unit.Dp) {
    val animated by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(400), label = "progress")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(TdcColors.TrackWhite),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .background(TdcColors.Brass),
        )
    }
}

private val SHORT_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
private val FULL_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d")

/** 今年なら "9/18"、それ以外は "2025/9/18"。 */
fun formatShortDate(date: LocalDate, today: LocalDate = LocalDate.now()): String =
    if (date.year == today.year) date.format(SHORT_DATE) else date.format(FULL_DATE)
