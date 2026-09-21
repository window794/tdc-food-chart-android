package com.broco.tdc_food_chart.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** アプリ設定（DataStore Preferences）。 */
data class AppSettings(
    /** 閉店したフードを「探す」一覧に表示するか。絞り込みシートの「閉店した店舗のフードも含める」と同じ値。 */
    val showClosed: Boolean = true,
    /** オンボーディングを表示済みか。 */
    val onboardingDone: Boolean = false,
)

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            showClosed = prefs[KEY_SHOW_CLOSED] ?: true,
            onboardingDone = prefs[KEY_ONBOARDING_DONE] ?: false,
        )
    }

    suspend fun setShowClosed(value: Boolean) {
        dataStore.edit { it[KEY_SHOW_CLOSED] = value }
    }

    suspend fun setOnboardingDone(value: Boolean) {
        dataStore.edit { it[KEY_ONBOARDING_DONE] = value }
    }

    private companion object {
        val KEY_SHOW_CLOSED = booleanPreferencesKey("show_closed")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }
}
