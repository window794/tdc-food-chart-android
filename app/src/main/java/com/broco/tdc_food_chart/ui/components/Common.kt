package com.broco.tdc_food_chart.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType
import java.text.NumberFormat
import java.util.Locale

/** 価格表示（"¥1,080"）。 */
fun formatYen(price: Int): String = "¥" + NumberFormat.getIntegerInstance(Locale.JAPAN).format(price)

/** 共通イージングに相当する持続時間（0.3s）。 */
const val MOTION_MS = 300

/** セクションラベル（英・italic・uppercase）。 */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text.uppercase(Locale.ROOT), style = TdcType.SectionLabel, modifier = modifier)
}

/** 1px の真鍮罫線。 */
@Composable
fun BrassDivider(modifier: Modifier = Modifier, color: Color = TdcColors.Brass22) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = color)
}

/**
 * pill 型チップ。選択時は Brass16 の面 + Brass75 の枠 + ChampagneStrong の文字。
 * タップ領域は 48dp。
 */
@Composable
fun TdcChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    role: Role = Role.Button,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
) {
    val bg by animateColorAsState(if (selected) TdcColors.Brass16 else Color.Transparent, tween(MOTION_MS), label = "chipBg")
    val border by animateColorAsState(if (selected) TdcColors.Brass75 else TdcColors.Brass28, tween(MOTION_MS), label = "chipBorder")
    val fg by animateColorAsState(if (selected) TdcColors.ChampagneStrong else TdcColors.ChipOff, tween(MOTION_MS), label = "chipFg")
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, border, CircleShape)
            .selectable(selected = selected, onClick = onClick, role = role)
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = TdcType.Chip, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * pill 型アクションボタン（詳細の「☆ お気に入り」「食べた」、シートの「条件をクリア」等）。
 * [filled] = 選択状態（Brass16 の面）。
 */
@Composable
fun PillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    emphasized: Boolean = false,
    enabled: Boolean = true,
    minHeight: Dp = 48.dp,
) {
    val bg by animateColorAsState(if (filled || emphasized) TdcColors.Brass16 else Color.Transparent, tween(MOTION_MS), label = "pillBg")
    val border by animateColorAsState(
        when {
            emphasized -> TdcColors.Brass60
            filled -> TdcColors.Brass75
            else -> TdcColors.Brass34
        },
        tween(MOTION_MS),
        label = "pillBorder",
    )
    val fg = when {
        emphasized || filled -> TdcColors.ChampagneStrong
        else -> TdcColors.ChampagneMuted
    }
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.defaultMinSize(minHeight = minHeight),
        shape = CircleShape,
        border = BorderStroke(1.dp, border),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor = fg,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TdcColors.TextWeak2,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(label, style = TdcType.Button, textAlign = TextAlign.Center, maxLines = 1)
    }
}

/** 角の小さいバッジ（コースター種別・閉店）。 */
@Composable
fun SmallBadge(text: String, modifier: Modifier = Modifier, closed: Boolean = false) {
    Text(
        text = text,
        style = TdcType.Badge,
        color = if (closed) TdcColors.ClosedText else TdcColors.ChampagnePale,
        modifier = modifier
            .border(1.dp, if (closed) TdcColors.ClosedBorder else TdcColors.Brass.copy(alpha = 0.32f), RoundedCornerShape(3.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
    )
}

/** 空表示（no results / no records）。 */
@Composable
fun EmptyState(title: String, note: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(title, style = TdcType.EmptyTitle, textAlign = TextAlign.Center)
        Text(note, style = TdcType.BodyMuted.copy(color = TdcColors.TextWeak2), textAlign = TextAlign.Center)
    }
}

/** 定義リストの 1 行（ラベル幅 64dp）。 */
@Composable
fun DefinitionRow(label: String, value: String, valueColor: Color = TdcColors.TextMid) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
        Text(label, style = TdcType.DefLabel, modifier = Modifier.width(64.dp))
        Text(value, style = TdcType.DefValue, color = valueColor, modifier = Modifier.weight(1f))
    }
}

/** 星（お気に入り）のトグルボタン。48dp のタップ領域。 */
@Composable
fun FavoriteButton(favorite: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val color by animateColorAsState(if (favorite) TdcColors.Champagne else TdcColors.IconMuted, tween(MOTION_MS), label = "fav")
    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(48.dp)
            .semantics { contentDescription = if (favorite) "お気に入りを解除" else "お気に入りに追加" },
    ) {
        StarIcon(filled = favorite, color = color, size = 14.dp)
    }
}

/** 縦の間隔。 */
@Composable
fun VSpace(height: Dp) = Spacer(Modifier.height(height))

/** 行方向の間隔。 */
@Composable
fun RowScope.HSpace(width: Dp) = Spacer(Modifier.width(width))

/** 弱い本文テキストのショートカット。 */
@Composable
fun MutedText(text: String, modifier: Modifier = Modifier, style: TextStyle = TdcType.Caption, textAlign: TextAlign? = null) {
    Text(text, style = style, modifier = modifier, textAlign = textAlign)
}
