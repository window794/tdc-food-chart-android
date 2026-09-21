package com.broco.tdc_food_chart.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.broco.tdc_food_chart.ui.intro.BrandIntro
import com.broco.tdc_food_chart.ui.intro.INTRO_CROSSFADE_MS
import com.broco.tdc_food_chart.ui.intro.TdcEasing
import com.broco.tdc_food_chart.ui.navigation.AppNavHost
import com.broco.tdc_food_chart.ui.onboarding.OnboardingScreen
import com.broco.tdc_food_chart.ui.theme.TdcColors
import com.broco.tdc_food_chart.ui.theme.TdcFoodChartTheme

/**
 * ルート。設定読み込み → ブランドイントロ（独立した全画面）→ （初回のみ）オンボーディング → ホーム。
 *
 * イントロの段階（[IntroPhase]）：
 * - PLAYING：イントロだけが画面にある。起動直後はホームをまだ組み立てない
 * - TRANSFER：ホームが下で組み立てられ opacity 0→1、イントロは 1→0（WEB 版と同じクロスフェード）
 * - DONE：イントロは消える
 * 設定タブの「ブランドイントロを再生」は PLAYING に戻すだけ。ホームは状態を保ったまま透明になり、同じ流れで戻ってくる。
 */
@Composable
fun TdcApp(appViewModel: AppViewModel) {
    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    val phase by appViewModel.introPhase.collectAsStateWithLifecycle()
    val foodCount by appViewModel.foodCount.collectAsStateWithLifecycle()

    // 一度でもホームを出したら、再生中も組み立てたままにする（NavHost やタブの状態を失わないため）
    var contentEverShown by rememberSaveable { mutableStateOf(false) }
    val showContent = contentEverShown || phase != IntroPhase.PLAYING

    val contentAlpha = remember { Animatable(if (phase == IntroPhase.DONE) 1f else 0f) }
    LaunchedEffect(phase) {
        if (phase != IntroPhase.PLAYING) contentEverShown = true
        when (phase) {
            IntroPhase.PLAYING -> contentAlpha.snapTo(0f)
            IntroPhase.TRANSFER -> contentAlpha.animateTo(1f, tween(INTRO_CROSSFADE_MS, easing = TdcEasing))
            IntroPhase.DONE -> contentAlpha.snapTo(1f)
        }
    }

    val navController = rememberNavController()

    TdcFoodChartTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TdcColors.Navy),
        ) {
            val current = settings
            if (current != null && showContent) {
                Box(Modifier.graphicsLayer { alpha = contentAlpha.value }) {
                    if (!current.onboardingDone) {
                        OnboardingScreen(foodCount = foodCount, onStart = appViewModel::finishOnboarding)
                    } else {
                        AppNavHost(navController = navController, appViewModel = appViewModel)
                    }
                }
            }
            if (phase != IntroPhase.DONE) {
                BrandIntro(
                    onTransfer = appViewModel::startIntroTransfer,
                    onFinished = appViewModel::finishIntro,
                )
            }
        }
    }
}
