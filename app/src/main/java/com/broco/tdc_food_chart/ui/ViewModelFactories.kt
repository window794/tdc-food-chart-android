package com.broco.tdc_food_chart.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.broco.tdc_food_chart.TdcFoodChartApplication
import com.broco.tdc_food_chart.di.AppContainer

/**
 * Application が持つ [AppContainer] から ViewModel を組み立てる小さなヘルパー。
 * Hilt を入れるほどの規模ではないので、各画面はこれ経由で ViewModel を取得する。
 */
@Composable
inline fun <reified VM : ViewModel> appViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    noinline create: (AppContainer, SavedStateHandle) -> VM,
): VM {
    val container = (LocalContext.current.applicationContext as TdcFoodChartApplication).container
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = viewModelFactory {
            initializer { create(container, createSavedStateHandle()) }
        },
    )
}
