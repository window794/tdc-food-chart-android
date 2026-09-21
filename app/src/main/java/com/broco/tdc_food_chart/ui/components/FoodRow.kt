package com.broco.tdc_food_chart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.data.model.Food
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/**
 * 一覧の 1 行。2a プロトタイプの行構成:
 *
 * ```
 * メニュー名                        ¥1,080
 * 店舗名
 * 考案者 ○○
 * [コースター]  ✓ 食べた　エリア  [閉店]  ☆
 * │ メモ抜粋（メモセグメントのみ）
 * ```
 *
 * 閉店は opacity .72。押下時は背景 #131E2E。
 */
@Composable
fun FoodRow(
    food: Food,
    favorite: Boolean,
    eaten: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    showEatenMark: Boolean = true,
    memoExcerpt: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (pressed) TdcColors.NavyPressed else TdcColors.Navy)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = TdcColors.Brass),
                role = Role.Button,
                onClick = onOpen,
            )
            .alpha(if (food.isClosed) 0.72f else 1f)
            .padding(start = 20.dp, top = 14.dp, end = 8.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(food.menu, style = TdcType.RowTitle, modifier = Modifier.weight(1f))
            Text(formatYen(food.price), style = TdcType.RowPrice, modifier = Modifier.padding(end = 12.dp, top = 2.dp))
        }
        Text(food.restaurant, style = TdcType.RowShop, modifier = Modifier.padding(end = 12.dp))
        // 考案者：店舗名の次に読める位置。ラベルは真鍮の小さな文字、名前は店舗名と同じ明度で 1 段小さく
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("考案者", style = TdcType.DefLabel.copy(fontSize = 10.sp, lineHeight = 16.sp))
            Text(
                food.author,
                style = TdcType.RowSub.copy(color = TdcColors.TextMid2, fontSize = 11.5.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 1.dp),
        ) {
            SmallBadge(food.coaster)
            val subline = if (showEatenMark && eaten) "✓ 食べた　${food.area}" else food.area
            Text(
                subline,
                style = TdcType.RowSub,
                color = if (showEatenMark && eaten) TdcColors.ChampagneMuted else TdcColors.TextWeak2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (food.isClosed) SmallBadge("閉店", closed = true)
            FavoriteButton(favorite = favorite, onToggle = onToggleFavorite)
        }
        if (!memoExcerpt.isNullOrBlank()) {
            Text(
                memoExcerpt.lineSequence().firstOrNull { it.isNotBlank() }?.trim() ?: "",
                style = TdcType.Caption.copy(color = TdcColors.TextWeak3),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .memoQuoteBorder(),
            )
        }
    }
}

/** メモ抜粋の左罫線（border-left 1px 真鍮 + padding-left 10px）。 */
private fun Modifier.memoQuoteBorder(): Modifier = this
    .background(TdcColors.Brass34)
    .padding(start = 1.dp)
    .background(TdcColors.Navy)
    .padding(start = 10.dp)
