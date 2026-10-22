package com.example.rangegen.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.rangegen.data.model.AppThemeSetting
import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RoundingSetting
import com.example.rangegen.data.model.UserSettings

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "rangegen_settings"

        private const val KEY_THEME = "theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"
        private const val KEY_DECIMAL_PLACES = "decimal_places"
        private const val KEY_ROUNDING_MODE = "rounding_mode"

        // Last input state keys
        private const val KEY_LAST_MODE = "last_mode"
        private const val KEY_LAST_START = "last_start"
        private const val KEY_LAST_END = "last_end"
        private const val KEY_LAST_POINTS = "last_points"
        private const val KEY_LAST_STEP = "last_step"
        private const val KEY_LAST_INCLUDE_END = "last_include_end"
    }

    fun loadSettings(): UserSettings {
        val themeStr = prefs.getString(KEY_THEME, AppThemeSetting.SYSTEM.name) ?: AppThemeSetting.SYSTEM.name
        val theme = try {
            AppThemeSetting.valueOf(themeStr)
        } catch (_: Exception) {
            AppThemeSetting.SYSTEM
        }

        val dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val animations = prefs.getBoolean(KEY_ANIMATIONS, true)
        val confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        val decimalPlaces = prefs.getInt(KEY_DECIMAL_PLACES, 10)

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingSetting.HALF_UP.name) ?: RoundingSetting.HALF_UP.name
        val rounding = try {
            RoundingSetting.valueOf(roundingStr)
        } catch (_: Exception) {
            RoundingSetting.HALF_UP
        }

        return UserSettings(
            theme = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = haptic,
            animations = animations,
            confirmDelete = confirmDelete,
            decimalPlaces = decimalPlaces,
            roundingMode = rounding
        )
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putBoolean(KEY_HAPTIC, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putInt(KEY_DECIMAL_PLACES, settings.decimalPlaces)
            .putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            .apply()
    }

    fun resetSettings(): UserSettings {
        val defaults = UserSettings()
        saveSettings(defaults)
        return defaults
    }

    fun saveLastInputs(
        mode: RangeMode,
        start: String,
        end: String,
        points: String,
        step: String,
        includeEnd: Boolean
    ) {
        prefs.edit()
            .putString(KEY_LAST_MODE, mode.name)
            .putString(KEY_LAST_START, start)
            .putString(KEY_LAST_END, end)
            .putString(KEY_LAST_POINTS, points)
            .putString(KEY_LAST_STEP, step)
            .putBoolean(KEY_LAST_INCLUDE_END, includeEnd)
            .apply()
    }

    fun loadLastMode(): RangeMode {
        val modeStr = prefs.getString(KEY_LAST_MODE, RangeMode.BY_POINTS.name) ?: RangeMode.BY_POINTS.name
        return try {
            RangeMode.valueOf(modeStr)
        } catch (_: Exception) {
            RangeMode.BY_POINTS
        }
    }

    fun loadLastStart(): String = prefs.getString(KEY_LAST_START, "0") ?: "0"
    fun loadLastEnd(): String = prefs.getString(KEY_LAST_END, "10") ?: "10"
    fun loadLastPoints(): String = prefs.getString(KEY_LAST_POINTS, "6") ?: "6"
    fun loadLastStep(): String = prefs.getString(KEY_LAST_STEP, "2") ?: "2"
    fun loadLastIncludeEnd(): Boolean = prefs.getBoolean(KEY_LAST_INCLUDE_END, true)
}
