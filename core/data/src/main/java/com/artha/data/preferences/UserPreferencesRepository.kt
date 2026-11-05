package com.artha.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "artha_user_preferences")

data class UserPreferences(
    val baseCurrency: String = "INR",
    val themeMode: String = "SYSTEM",
    val uiDensity: String = "COMFORTABLE",
    val numberGroupingStyle: String = "AUTO",
    val isBiometricEnabled: Boolean = false,
    val isHideBalancesInRecents: Boolean = false,
    val monthStartDay: Int = 1,
    val weekStartDay: Int = 2, // Monday
    val linkedGoogleAccount: String? = null,
    val lastBackupTimestamp: Long = 0L,
    val isAutoDriveBackupEnabled: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            baseCurrency = prefs[KEY_BASE_CURRENCY] ?: "INR",
            themeMode = prefs[KEY_THEME_MODE] ?: "SYSTEM",
            uiDensity = prefs[KEY_UI_DENSITY] ?: "COMFORTABLE",
            numberGroupingStyle = prefs[KEY_NUMBER_GROUPING] ?: "AUTO",
            isBiometricEnabled = prefs[KEY_BIOMETRIC_ENABLED] ?: false,
            isHideBalancesInRecents = prefs[KEY_HIDE_BALANCES_RECENTS] ?: false,
            monthStartDay = prefs[KEY_MONTH_START_DAY] ?: 1,
            weekStartDay = prefs[KEY_WEEK_START_DAY] ?: 2,
            linkedGoogleAccount = prefs[KEY_GOOGLE_ACCOUNT],
            lastBackupTimestamp = prefs[KEY_LAST_BACKUP_TIMESTAMP] ?: 0L,
            isAutoDriveBackupEnabled = prefs[KEY_AUTO_DRIVE_BACKUP] ?: false
        )
    }

    suspend fun updateBaseCurrency(currency: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BASE_CURRENCY] = currency
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode
        }
    }

    suspend fun updateUiDensity(density: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_UI_DENSITY] = density
        }
    }

    suspend fun updateNumberGroupingStyle(style: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NUMBER_GROUPING] = style
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setHideBalancesInRecents(hide: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HIDE_BALANCES_RECENTS] = hide
        }
    }

    suspend fun updateSalaryCycleStartDay(day: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MONTH_START_DAY] = day.coerceIn(1, 31)
        }
    }

    suspend fun linkGoogleAccount(email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GOOGLE_ACCOUNT] = email
        }
    }

    suspend fun unlinkGoogleAccount() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_GOOGLE_ACCOUNT)
        }
    }

    suspend fun recordBackup(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_BACKUP_TIMESTAMP] = timestamp
        }
    }

    suspend fun setAutoDriveBackupEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_DRIVE_BACKUP] = enabled
        }
    }

    companion object {
        private val KEY_BASE_CURRENCY = stringPreferencesKey("pref_base_currency")
        private val KEY_THEME_MODE = stringPreferencesKey("pref_theme_mode")
        private val KEY_UI_DENSITY = stringPreferencesKey("pref_ui_density")
        private val KEY_NUMBER_GROUPING = stringPreferencesKey("pref_number_grouping")
        private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("pref_biometric_enabled")
        private val KEY_HIDE_BALANCES_RECENTS = booleanPreferencesKey("pref_hide_balances_recents")
        private val KEY_MONTH_START_DAY = intPreferencesKey("pref_month_start_day")
        private val KEY_WEEK_START_DAY = intPreferencesKey("pref_week_start_day")
        private val KEY_GOOGLE_ACCOUNT = stringPreferencesKey("pref_google_account")
        private val KEY_LAST_BACKUP_TIMESTAMP = longPreferencesKey("pref_last_backup_timestamp")
        private val KEY_AUTO_DRIVE_BACKUP = booleanPreferencesKey("pref_auto_drive_backup")
    }
}
