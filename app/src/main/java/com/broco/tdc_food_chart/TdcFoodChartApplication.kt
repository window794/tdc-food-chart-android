package com.broco.tdc_food_chart

import android.app.Application
import com.broco.tdc_food_chart.di.AppContainer

class TdcFoodChartApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
