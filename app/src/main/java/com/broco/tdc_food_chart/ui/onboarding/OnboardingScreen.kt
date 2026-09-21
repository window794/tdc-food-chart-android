package com.broco.tdc_food_chart.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.ui.components.PillButton
import com.broco.tdc_food_chart.ui.components.StarField
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * 初回のみ表示する 1 画面のオンボーディング（ブランドイントロの後に出る）。
 * スプラッシュのロックアップを上に残し、4 行の概要と「はじめる」。スキップ導線は置かない。
 * 操作の詳細はここでは説明せず、「使い方 / ヘルプ」（HelpScreen）に任せる。
 */
@Composable
fun OnboardingScreen(foodCount: Int, onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TdcColors.Navy),
    ) {
        StarField(modifier = Modifier.fillMaxSize(), alpha = 0.10f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterVertically),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("TDC FOOD CHART", style = TdcType.BrandTitleLarge, maxLines = 1, softWrap = false)
                Text("探る昼とめぐる夜", style = TdcType.BrandSubLarge)
            }

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                val count = if (foodCount > 0) "$foodCount 品の" else ""
                OnboardingLine("01", "${count}コラボフードを、メニュー・店舗・考案者・エリアから探せます。")
                OnboardingLine("02", "お気に入り・食べた記録・メモ・写真を残せます。食べた日はあとから変えられ、写真に撮影日があればその日を候補にできます。")
                OnboardingLine("03", "観測記録で、食べた数やエリア別の進捗を振り返れます。")
                OnboardingLine("04", "記録と写真はこの端末の中だけに保存され、検索や記録の確認は電波のない場所でもできます。")
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                PillButton(
                    label = "はじめる　→",
                    onClick = onStart,
                    emphasized = true,
                    minHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "操作方法は下の「設定」タブ →「使い方 / ヘルプ」でいつでも確認できます。\n非公式ツールです。TDC および株式会社 baton とは関係がありません。",
                    style = TdcType.Caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OnboardingLine(number: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
        Text(number, style = TdcType.Ordinal, modifier = Modifier.width(20.dp))
        Text(text, style = TdcType.Body.copy(color = TdcColors.TextMid2, lineHeight = 23.sp), modifier = Modifier.weight(1f))
    }
}
