package com.broco.tdc_food_chart.ui.intro

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broco.tdc_food_chart.ui.components.SkyCharts
import com.broco.tdc_food_chart.ui.components.StarField
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcType

/** WEB 版と共通のイージング cubic-bezier(.22,.61,.36,1)。 */
val TdcEasing: Easing = CubicBezierEasing(0.22f, 0.61f, 0.36f, 1f)

/*
 * WEB 版（https://window794.github.io/tdc-food-chart/）のブランドイントロを実測した値：
 *   splash = fixed 全画面・#0E1724、sky opacity .62
 *   star   : starIn  .9s ease-out、星ごとに delay .04〜.58s（opacity 0→1）
 *   line   : traceIn 1.05s、線ごとに delay .42〜.78s（stroke-dashoffset 420→0 + opacity 0→1）
 *   title  : focusIn .68s delay .40s（opacity 0→1、blur 9px→0、scale 1.014→1）
 *   sub    : softIn  .52s delay .82s（opacity 0→1、blur 4px→0）
 *   hold   : 〜2.2s
 *   transfer: splash opacity 1→0（.66s）、lockup scale→.994（1s）、同時に app opacity 0→1（.66s）
 *   合計 2.86s。タイトルは移動しない。主役はクロスフェード。
 *
 * Android 版はこの構図・順序・イージングを保ったまま、時間軸を 0.55 倍にして約 1.57s に再構成する。
 */
private const val SCALE = 0.55f
private fun ms(webSeconds: Float): Int = (webSeconds * 1000 * SCALE).toInt()

/** 星ごとの遅延（WEB 版の --d をそのまま転記、29 個）。 */
private val STAR_DELAYS = floatArrayOf(
    .1f, .04f, .22f, .3f, .36f, .08f, .16f, .26f, .34f, .2f, .12f, .3f, .4f, .24f, .32f,
    .42f, .5f, .14f, .06f, .28f, .44f, .36f, .52f, .46f, .38f, .56f, .48f, .4f, .58f,
)

/** 星座線ごとの遅延（WEB 版の --d、8 本）。 */
private val LINE_DELAYS = floatArrayOf(.42f, .55f, .62f, .7f, .5f, .66f, .74f, .78f)

private val STAR_DURATION = ms(.9f)
private val LINE_DURATION = ms(1.05f)
private val TITLE_DELAY = ms(.40f)
private val TITLE_DURATION = ms(.68f)
private val SUB_DELAY = ms(.82f)
private val SUB_DURATION = ms(.52f)
private val TRANSFER_START = ms(2.2f)               // 1210ms
private val TRANSFER_FADE = ms(.66f)                // 363ms
private val LOCKUP_SCALE_DURATION = ms(1.0f)
private val TOTAL = TRANSFER_START + TRANSFER_FADE  // 1573ms
private const val REDUCED_HOLD = 320
private const val REDUCED_FADE = 180

/** クロスフェードの長さ。ホーム側のフェードインもこれに合わせる。 */
val INTRO_CROSSFADE_MS: Int get() = TRANSFER_FADE

/**
 * ブランドイントロ。独立した全画面（ホームの上に暗幕を重ねる方式ではない）。
 * [onTransfer] でクロスフェード開始を知らせ（このときホームが下で描かれ始める）、
 * [onFinished] で完全に消える。起動時も「ブランドイントロを再生」も同じこの画面。
 */
@Composable
fun BrandIntro(onTransfer: () -> Unit, onFinished: () -> Unit) {
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val total = if (reduceMotion) REDUCED_HOLD + REDUCED_FADE else TOTAL
    val transferStart = if (reduceMotion) REDUCED_HOLD else TRANSFER_START
    val transferFade = if (reduceMotion) REDUCED_FADE else TRANSFER_FADE
    val clock = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 時間軸を 1 本の線形クロックで進め、各要素はそこから自分の区間を切り出す
        clock.animateTo(transferStart.toFloat(), tween(durationMillis = transferStart, easing = LinearEasing))
        onTransfer()
        clock.animateTo(total.toFloat(), tween(durationMillis = transferFade, easing = LinearEasing))
        onFinished()
    }

    val t = clock.value
    val splashAlpha: Float
    val lockupScale: Float
    val titleAlpha: Float
    val titleBlur: Float
    val titleScale: Float
    val subAlpha: Float
    val subBlur: Float
    val starAt: (Int) -> Float
    val lineAt: (Int) -> Float
    if (reduceMotion) {
        splashAlpha = 1f - seg(t, REDUCED_HOLD, REDUCED_FADE, LinearEasing)
        lockupScale = 1f
        titleAlpha = 1f
        titleBlur = 0f
        titleScale = 1f
        subAlpha = 1f
        subBlur = 0f
        starAt = { 1f }
        lineAt = { 1f }
    } else {
        val title = seg(t, TITLE_DELAY, TITLE_DURATION, TdcEasing)
        titleAlpha = title
        titleBlur = 9f * (1f - title)
        titleScale = 1.014f - 0.014f * title
        val sub = seg(t, SUB_DELAY, SUB_DURATION, TdcEasing)
        subAlpha = sub
        subBlur = 4f * (1f - sub)
        starAt = { i -> seg(t, ms(STAR_DELAYS[i % STAR_DELAYS.size]), STAR_DURATION, FastOutSlowInEasing) }
        lineAt = { i -> seg(t, ms(LINE_DELAYS[i % LINE_DELAYS.size]), LINE_DURATION, TdcEasing) }
        splashAlpha = 1f - seg(t, TRANSFER_START, TRANSFER_FADE, TdcEasing)
        // WEB 版 .lockup { transform: scale(.994); transition: 1s } の写し。ほぼ止まって見える程度
        lockupScale = 1f - 0.006f * seg(t, TRANSFER_START, LOCKUP_SCALE_DURATION, TdcEasing)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = splashAlpha }
            .background(TdcColors.Navy)
            // 再生中は下の画面へのタップを通さない（WEB 版の inert 相当）
            .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
            .clearAndSetSemantics { },
    ) {
        StarField(
            modifier = Modifier.fillMaxSize(),
            viewBox = SkyCharts.SPLASH_VIEWBOX,
            lines = SkyCharts.SPLASH_LINES,
            stars = SkyCharts.SPLASH_STARS,
            alpha = 0.62f,
            starProgressAt = starAt,
            lineProgressAt = lineAt,
        )
        // ロックアップ。最初から最終位置・最終幅にあり、途中で動かない（WEB 版と同じ）
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp)
                .graphicsLayer {
                    scaleX = lockupScale
                    scaleY = lockupScale
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // WEB mobile: 300 clamp(18px,5.9vw,30px)/1.25、letter-spacing .16em（390px 幅で約 23px → 24sp）
            Text(
                "TDC FOOD CHART",
                style = TdcType.BrandTitleLarge.copy(fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = 3.84.sp),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha
                        scaleX = titleScale
                        scaleY = titleScale
                    }
                    .blur(titleBlur.dp),
            )
            // WEB: margin-top 18px、400 12px/1.9、letter-spacing .2em、真鍮
            Box(Modifier.height(18.dp))
            Text(
                "探る昼とめぐる夜",
                style = TdcType.BrandSubLarge.copy(letterSpacing = 2.4.sp),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .graphicsLayer { alpha = subAlpha }
                    .blur(subBlur.dp),
            )
        }
    }
}

/** clock の値 [t] を、[start] から [duration] の区間で 0..1 に写像する。 */
private fun seg(t: Float, start: Int, duration: Int, easing: Easing): Float {
    if (duration <= 0) return if (t >= start) 1f else 0f
    val raw = ((t - start) / duration).coerceIn(0f, 1f)
    return easing.transform(raw)
}
