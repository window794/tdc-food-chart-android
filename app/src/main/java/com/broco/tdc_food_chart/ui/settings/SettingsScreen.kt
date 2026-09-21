package com.broco.tdc_food_chart.ui.settings

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.ui.components.BrassDivider
import com.broco.tdc_food_chart.ui.components.SectionLabel
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * 「設定」タブ。以前は右上 ⋮ のボトムシートだったが、項目が増えたので 4 つ目のタブに昇格した。
 * 項目：使い方 / ヘルプ、閉店したフードを表示、ブランドイントロを再生、このアプリについて。
 * 「オフラインデータ更新」「記録の書き出し・取り込み」は未実装のため置かない（動くように見せかけない）。
 */
@Composable
fun SettingsScreen(
    showClosed: Boolean,
    foodCount: Int,
    scrollState: ScrollState,
    onShowClosedChange: (Boolean) -> Unit,
    onReplayIntro: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var aboutOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
    ) {
        SectionLabel("Settings ／ 設定", modifier = Modifier.padding(bottom = 8.dp))

        SettingsRow(title = "使い方 / ヘルプ", subtitle = "探す・記録・写真・観測記録の操作方法", onClick = onOpenHelp)
        BrassDivider(color = TdcColors.Brass16)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .toggleable(value = showClosed, role = Role.Switch, onValueChange = onShowClosedChange)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                Text("閉店したフードを表示", style = TdcType.Body)
                Text("探すタブの一覧に閉店した店舗のフードも並べます", style = TdcType.Caption)
            }
            Switch(
                checked = showClosed,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = TdcColors.ChampagneStrong,
                    checkedTrackColor = TdcColors.Brass.copy(alpha = 0.3f),
                    checkedBorderColor = TdcColors.Brass50,
                    uncheckedThumbColor = TdcColors.Placeholder,
                    uncheckedTrackColor = TdcColors.Sheet,
                    uncheckedBorderColor = TdcColors.Brass50,
                ),
            )
        }
        BrassDivider(color = TdcColors.Brass16)

        SettingsRow(title = "ブランドイントロを再生", subtitle = "起動時と同じイントロをもう一度見る", onClick = onReplayIntro)
        BrassDivider(color = TdcColors.Brass16)

        SettingsRow(title = "このアプリについて", onClick = { aboutOpen = true })

        Text(
            "写真・メモ・食べた記録はこの端末内にだけ保存され、外部には送信されません。",
            style = TdcType.Caption,
            modifier = Modifier.padding(top = 18.dp),
        )
    }

    if (aboutOpen) {
        AboutDialog(foodCount = foodCount, onDismiss = { aboutOpen = false })
    }
}

@Composable
private fun SettingsRow(title: String, onClick: () -> Unit, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(title, style = TdcType.Body)
            if (subtitle != null) Text(subtitle, style = TdcType.Caption)
        }
        Text("→", style = TdcType.Body, color = TdcColors.IconMuted)
    }
}

@Composable
private fun AboutDialog(foodCount: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = TdcColors.Sheet,
        titleContentColor = TdcColors.Heading,
        textContentColor = TdcColors.TextMid,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("TDC FOOD CHART", style = TdcType.BrandTitle.copy(fontSize = 20.sp, lineHeight = 26.sp))
                Text("探る昼とめぐる夜", style = TdcType.BrandSub)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "トーキョー・ディスカバリー・シティのコラボフードを検索し、お気に入り・食べた記録・食べた日・メモ・写真を端末内に残すための非公式アプリです。",
                    style = TdcType.Body,
                )
                Text(
                    "収録データ：$foodCount 品（filtered_data.json）\n閉店判定は掲載当時の情報にもとづきます。",
                    style = TdcType.BodyMuted,
                )
                Text(
                    "個人の記録は外部サービスへ送信されません。基本機能はオフラインでも利用できます。\n非公式ツールです。TDC および株式会社 baton とは関係がありません。",
                    style = TdcType.Caption,
                )
                Text(
                    "書体：Zen Old Mincho ／ Cormorant Garamond（SIL Open Font License）",
                    style = TdcType.Caption,
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("閉じる", color = TdcColors.ChampagneStrong) } },
    )
}
