package com.broco.tdc_food_chart

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.broco.tdc_food_chart.ui.AppViewModel
import com.broco.tdc_food_chart.ui.TdcApp
import com.broco.tdc_food_chart.ui.appViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // OS 標準のスプラッシュ。設定（DataStore）が読めるまで保持し、その後 in-app のブランドイントロへ引き継ぐ
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // 常にダーク背景なので、システムバーのアイコンは明るい色で固定する
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        var settingsLoaded = false
        splash.setKeepOnScreenCondition { !settingsLoaded }
        setContent {
            val appViewModel = appViewModel<AppViewModel> { container, _ ->
                AppViewModel(container.settingsRepository, container.foodRepository)
            }
            val settings by appViewModel.settings.collectAsStateWithLifecycle()
            if (settings != null) settingsLoaded = true
            TdcApp(appViewModel = appViewModel)
        }
    }
}
