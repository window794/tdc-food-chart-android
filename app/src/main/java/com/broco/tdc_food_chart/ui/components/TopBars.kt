package com.broco.tdc_food_chart.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * タブ画面のヘッダー。タイトル＋サブのみ（設定は下部タブになったので右上のメニューは無い）。
 * 探す: "TDC FOOD CHART / 探る昼とめぐる夜"、コレクション: "Collection / お気に入り・食べた・メモ"、
 * 観測記録: "Observation log / 制覇率と観測記録"、設定: "Settings / 使い方・表示・このアプリについて"。
 */
@Composable
fun TabTopBar(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(title, style = TdcType.BrandTitle, maxLines = 1, softWrap = false, modifier = Modifier.semantics { heading() })
        Text(subtitle, style = TdcType.BrandSub, maxLines = 1)
    }
}

/** 詳細画面のヘッダー。← と小さなラベル。 */
@Composable
fun DetailTopBar(label: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "戻る" },
        ) {
            BackIcon(color = TdcColors.ChampagnePale)
        }
        Text(label, style = TdcType.DefLabel.copy(color = TdcColors.TextWeak2))
    }
}
