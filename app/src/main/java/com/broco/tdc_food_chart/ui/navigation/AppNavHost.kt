package com.broco.tdc_food_chart.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.broco.tdc_food_chart.ui.AppViewModel
import com.broco.tdc_food_chart.ui.appViewModel
import com.broco.tdc_food_chart.ui.detail.FoodDetailScreen
import com.broco.tdc_food_chart.ui.detail.FoodDetailViewModel
import com.broco.tdc_food_chart.ui.help.HelpAction
import com.broco.tdc_food_chart.ui.help.HelpScreen
import com.broco.tdc_food_chart.ui.home.HomeScreen
import com.broco.tdc_food_chart.ui.intro.TdcEasing
import kotlinx.serialization.Serializable

/** ホーム（3 タブ）。 */
@Serializable
object HomeRoute

/** フード詳細。前面に積み、システムバックで戻る。 */
@Serializable
data class FoodDetailRoute(val foodId: String)

/** 使い方 / ヘルプ。設定タブから開く。 */
@Serializable
object HelpRoute

/** ヘルプ → ホームのタブ移動を渡すための SavedStateHandle キー。 */
const val KEY_REQUEST_TAB = "request_tab"
const val KEY_REQUEST_SEGMENT = "request_segment"

/**
 * 画面遷移。ホーム（タブ）と詳細の 2 階層だけ。
 * 詳細は右から 16dp スライド＋フェード（2a の screenIn .28s 相当）。
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = { fadeIn(tween(280, easing = TdcEasing)) + slideInHorizontally(tween(280, easing = TdcEasing)) { it / 24 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) },
        popExitTransition = { fadeOut(tween(220, easing = TdcEasing)) + slideOutHorizontally(tween(220, easing = TdcEasing)) { it / 24 } },
    ) {
        composable<HomeRoute> { entry ->
            HomeScreen(
                appViewModel = appViewModel,
                navRequests = entry.savedStateHandle,
                onOpenFood = { foodId -> navController.navigate(FoodDetailRoute(foodId)) },
                onOpenHelp = { navController.navigate(HelpRoute) },
            )
        }
        composable<HelpRoute> {
            HelpScreen(
                onBack = { navController.popBackStack() },
                onAction = { action ->
                    when (action) {
                        is HelpAction.OpenTab -> {
                            // ホームの SavedStateHandle に要求を置いてから戻る。Home 側が拾ってタブを切り替える
                            navController.getBackStackEntry<HomeRoute>().savedStateHandle.apply {
                                set(KEY_REQUEST_TAB, action.tab.name)
                                set(KEY_REQUEST_SEGMENT, action.segment?.name)
                            }
                            navController.popBackStack<HomeRoute>(inclusive = false)
                        }
                    }
                },
            )
        }
        composable<FoodDetailRoute> { entry ->
            val route = entry.toRoute<FoodDetailRoute>()
            val viewModel = appViewModel<FoodDetailViewModel> { container, _ ->
                FoodDetailViewModel(
                    foodId = route.foodId,
                    foodRepository = container.foodRepository,
                    userRecordRepository = container.userRecordRepository,
                    photoRepository = container.photoRepository,
                    appScope = container.appScope,
                )
            }
            FoodDetailScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
