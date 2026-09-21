package com.broco.tdc_food_chart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.broco.tdc_food_chart.ui.theme.TdcColors
import kotlin.math.max

/** 星座（線でつなぐ点列）。座標は viewBox 基準。 */
class Constellation(vararg points: Pair<Float, Float>) {
    val points: List<Offset> = points.map { Offset(it.first, it.second) }
}

/** 星（座標と半径）。座標は viewBox 基準。 */
data class Star(val x: Float, val y: Float, val r: Float)

/**
 * 天球図モチーフの幾何。WEB 版 v1.0 のインライン SVG から転記した（README の指示どおり）。
 * 装飾であり情報ではないので、TalkBack には読ませない。
 */
object SkyCharts {
    /** メイン画面背景用（viewBox 1440×1040）。 */
    val MAIN_VIEWBOX = 1440f to 1040f
    val MAIN_LINES = listOf(
        Constellation(118f to 128f, 196f to 96f, 268f to 140f, 214f to 62f, 158f to 44f),
        Constellation(1258f to 88f, 1334f to 126f, 1382f to 72f, 1300f to 44f, 1246f to 58f),
        Constellation(296f to 902f, 372f to 866f, 420f to 918f, 482f to 880f, 404f to 812f),
        Constellation(1106f to 774f, 1162f to 818f, 1234f to 790f, 1286f to 842f, 1198f to 870f, 1140f to 916f),
        Constellation(642f to 62f, 708f to 34f, 748f to 96f, 820f to 66f),
        Constellation(60f to 520f, 112f to 566f, 88f to 638f, 152f to 606f),
        Constellation(1372f to 452f, 1318f to 498f, 1364f to 560f),
        Constellation(836f to 968f, 902f to 934f, 964f to 976f),
    )
    val MAIN_STARS = listOf(
        Star(118f, 128f, 1.5f), Star(196f, 96f, 2.2f), Star(268f, 140f, 1.4f), Star(214f, 62f, 1.6f), Star(158f, 44f, 1.3f),
        Star(1258f, 88f, 1.4f), Star(1334f, 126f, 2.1f), Star(1382f, 72f, 1.4f), Star(1300f, 44f, 1.6f), Star(1246f, 58f, 1.2f),
        Star(296f, 902f, 1.6f), Star(372f, 866f, 2.3f), Star(420f, 918f, 1.4f), Star(482f, 880f, 1.5f), Star(404f, 812f, 1.3f),
        Star(1106f, 774f, 1.5f), Star(1162f, 818f, 1.4f), Star(1234f, 790f, 2.2f), Star(1286f, 842f, 1.3f), Star(1198f, 870f, 1.5f), Star(1140f, 916f, 1.3f),
        Star(642f, 62f, 1.4f), Star(708f, 34f, 2f), Star(748f, 96f, 1.3f), Star(820f, 66f, 1.5f),
        Star(60f, 520f, 1.3f), Star(112f, 566f, 1.8f), Star(88f, 638f, 1.3f), Star(152f, 606f, 1.4f),
        Star(1372f, 452f, 1.4f), Star(1318f, 498f, 1.7f), Star(1364f, 560f, 1.3f),
        Star(836f, 968f, 1.3f), Star(902f, 934f, 1.9f), Star(964f, 976f, 1.3f),
        Star(520f, 196f, 1.1f), Star(948f, 260f, 1.2f), Star(240f, 392f, 1.1f), Star(1180f, 330f, 1.1f), Star(700f, 690f, 1.1f),
    )

    /** スプラッシュ用（viewBox 900×620）。 */
    val SPLASH_VIEWBOX = 900f to 620f
    val SPLASH_LINES = listOf(
        Constellation(96f to 118f, 168f to 88f, 232f to 128f, 184f to 56f, 132f to 40f),
        Constellation(742f to 96f, 804f to 132f, 856f to 84f, 788f to 52f),
        Constellation(120f to 498f, 186f to 464f, 232f to 512f, 296f to 478f),
        Constellation(646f to 486f, 704f to 524f, 772f to 496f, 820f to 546f),
        Constellation(396f to 44f, 462f to 22f, 506f to 74f),
        Constellation(60f to 296f, 104f to 334f, 78f to 384f),
        Constellation(842f to 288f, 806f to 330f, 846f to 376f),
        Constellation(430f to 576f, 494f to 552f, 548f to 588f),
    )
    val SPLASH_STARS = listOf(
        Star(96f, 118f, 1.5f), Star(168f, 88f, 2.3f), Star(232f, 128f, 1.4f), Star(184f, 56f, 1.6f), Star(132f, 40f, 1.3f),
        Star(742f, 96f, 1.5f), Star(804f, 132f, 2.2f), Star(856f, 84f, 1.4f), Star(788f, 52f, 1.5f),
        Star(120f, 498f, 1.5f), Star(186f, 464f, 2.1f), Star(232f, 512f, 1.3f), Star(296f, 478f, 1.5f),
        Star(646f, 486f, 1.4f), Star(704f, 524f, 1.9f), Star(772f, 496f, 1.4f), Star(820f, 546f, 1.3f),
        Star(396f, 44f, 1.4f), Star(462f, 22f, 2f), Star(506f, 74f, 1.3f),
        Star(60f, 296f, 1.3f), Star(104f, 334f, 1.8f), Star(78f, 384f, 1.3f),
        Star(842f, 288f, 1.4f), Star(806f, 330f, 1.7f), Star(846f, 376f, 1.3f),
        Star(430f, 576f, 1.4f), Star(494f, 552f, 1.9f), Star(548f, 588f, 1.3f),
    )
}

/**
 * 天球図を描く。`preserveAspectRatio="xMidYMid slice"` 相当（cover）でスケールする。
 *
 * @param alpha グループ全体の不透明度（メイン背景 0.10、スプラッシュ 0.62）。
 * @param starProgressAt 星ごとの出現進捗 0..1（index → 進捗）。WEB 版の starIn（星ごとに遅延）用。省略時は全表示。
 * @param lineProgressAt 星座線ごとの描画進捗 0..1（stroke-dashoffset 相当）。省略時は全表示。
 */
@Composable
fun StarField(
    modifier: Modifier = Modifier,
    viewBox: Pair<Float, Float> = SkyCharts.MAIN_VIEWBOX,
    lines: List<Constellation> = SkyCharts.MAIN_LINES,
    stars: List<Star> = SkyCharts.MAIN_STARS,
    alpha: Float = 0.10f,
    starProgressAt: ((Int) -> Float)? = null,
    lineProgressAt: ((Int) -> Float)? = null,
) {
    Canvas(modifier = modifier.clearAndSetSemantics { }) {
        drawSky(viewBox, lines, stars, alpha, starProgressAt, lineProgressAt)
    }
}

private fun DrawScope.drawSky(
    viewBox: Pair<Float, Float>,
    lines: List<Constellation>,
    stars: List<Star>,
    alpha: Float,
    starProgressAt: ((Int) -> Float)?,
    lineProgressAt: ((Int) -> Float)?,
) {
    if (alpha <= 0f) return
    val (vw, vh) = viewBox
    val scale = max(size.width / vw, size.height / vh)
    val dx = (size.width - vw * scale) / 2f
    val dy = (size.height - vh * scale) / 2f
    fun map(p: Offset) = Offset(dx + p.x * scale, dy + p.y * scale)

    val strokeWidth = 0.65f * scale.coerceAtLeast(1f)
    lines.forEachIndexed { index, c ->
        val progress = lineProgressAt?.invoke(index)?.coerceIn(0f, 1f) ?: 1f
        if (progress <= 0f) return@forEachIndexed
        val path = Path()
        c.points.forEachIndexed { i, p ->
            val m = map(p)
            if (i == 0) path.moveTo(m.x, m.y) else path.lineTo(m.x, m.y)
        }
        // WEB 版 traceIn：opacity 0→1 と stroke-dashoffset 420→0 を同時に進める
        val color = TdcColors.Brass.copy(alpha = alpha * progress)
        if (progress >= 1f) {
            drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        } else {
            var length = 0f
            for (i in 1 until c.points.size) length += (map(c.points[i]) - map(c.points[i - 1])).getDistance()
            drawPath(
                path,
                color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(length * progress, length + 1f), 0f),
                ),
            )
        }
    }

    stars.forEachIndexed { index, s ->
        val progress = starProgressAt?.invoke(index)?.coerceIn(0f, 1f) ?: 1f
        if (progress <= 0f) return@forEachIndexed
        drawCircle(
            TdcColors.Champagne.copy(alpha = alpha * progress),
            radius = s.r * scale.coerceAtLeast(1f),
            center = map(Offset(s.x, s.y)),
        )
    }
}
