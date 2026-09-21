package com.broco.tdc_food_chart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * アイコンは絵文字・アイコンフォントを使わず、デザインの記号（◎ ★ ◍ ← × →）と設定の歯車を線で描く。
 * いずれも装飾なので semantics は空にし、意味は親（IconButton 等）の contentDescription が持つ。
 */

/** ◎ 探す。二重の円。 */
@Composable
fun SearchIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val stroke = Stroke(width = 1.2.dp.toPx())
        val c = center
        drawCircle(color, radius = this.size.minDimension / 2 - stroke.width, center = c, style = stroke)
        drawCircle(color, radius = this.size.minDimension / 4.2f, center = c, style = stroke)
    }
}

/** ★／☆。 */
@Composable
fun StarIcon(filled: Boolean, color: Color, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val outer = this.size.minDimension / 2
        val inner = outer * 0.42f
        val path = Path()
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outer else inner
            val angle = -PI / 2 + i * PI / 5
            val p = Offset(center.x + (r * cos(angle)).toFloat(), center.y + (r * sin(angle)).toFloat())
            if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        path.close()
        if (filled) drawPath(path, color) else drawPath(path, color, style = Stroke(width = 1.1.dp.toPx()))
    }
}

/** ◍ 観測記録。円の中に半分の塗り（星図を埋めていくイメージ）。 */
@Composable
fun LogIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val stroke = Stroke(width = 1.2.dp.toPx())
        val r = this.size.minDimension / 2 - stroke.width
        drawCircle(color, radius = r, center = center, style = stroke)
        val inner = r - 2.2.dp.toPx()
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(center.x - inner, center.y - inner),
            size = androidx.compose.ui.geometry.Size(inner * 2, inner * 2),
        )
    }
}

/** ← 戻る。 */
@Composable
fun BackIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 18.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val w = 1.4.dp.toPx()
        val y = center.y
        val x0 = this.size.width * 0.15f
        val x1 = this.size.width * 0.85f
        drawLine(color, Offset(x0, y), Offset(x1, y), strokeWidth = w, cap = StrokeCap.Round)
        val h = this.size.height * 0.28f
        drawLine(color, Offset(x0, y), Offset(x0 + h, y - h), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(color, Offset(x0, y), Offset(x0 + h, y + h), strokeWidth = w, cap = StrokeCap.Round)
    }
}

/** × 閉じる・消す。 */
@Composable
fun CloseIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val w = 1.3.dp.toPx()
        val inset = this.size.minDimension * 0.18f
        drawLine(color, Offset(inset, inset), Offset(this.size.width - inset, this.size.height - inset), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(color, Offset(this.size.width - inset, inset), Offset(inset, this.size.height - inset), strokeWidth = w, cap = StrokeCap.Round)
    }
}

/** 設定。細い歯車（円＋8 本の短い歯）。他のタブアイコンと同じ線の太さ。 */
@Composable
fun SettingsIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val stroke = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
        val outer = this.size.minDimension / 2 - stroke.width
        val ring = outer * 0.62f
        drawCircle(color, radius = ring, center = center, style = stroke)
        drawCircle(color, radius = outer * 0.2f, center = center, style = stroke)
        for (i in 0 until 8) {
            val a = i * PI / 4
            val from = Offset(center.x + (ring * cos(a)).toFloat(), center.y + (ring * sin(a)).toFloat())
            val to = Offset(center.x + (outer * cos(a)).toFloat(), center.y + (outer * sin(a)).toFloat())
            drawLine(color, from, to, strokeWidth = stroke.width, cap = StrokeCap.Round)
        }
    }
}

/** ＋ 追加。 */
@Composable
fun PlusIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val w = 1.2.dp.toPx()
        val inset = this.size.minDimension * 0.2f
        drawLine(color, Offset(center.x, inset), Offset(center.x, this.size.height - inset), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(color, Offset(inset, center.y), Offset(this.size.width - inset, center.y), strokeWidth = w, cap = StrokeCap.Round)
    }
}

/** ✓ チェック。 */
@Composable
fun CheckIcon(color: Color, modifier: Modifier = Modifier, size: Dp = 12.dp) {
    Canvas(modifier.size(size).clearAndSetSemantics { }) {
        val w = 1.4.dp.toPx()
        val s = this.size
        drawLine(color, Offset(s.width * 0.15f, s.height * 0.55f), Offset(s.width * 0.4f, s.height * 0.8f), strokeWidth = w, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.4f, s.height * 0.8f), Offset(s.width * 0.88f, s.height * 0.22f), strokeWidth = w, cap = StrokeCap.Round)
    }
}
