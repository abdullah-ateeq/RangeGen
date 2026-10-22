package com.example.rangegen.data.model

import java.math.RoundingMode

enum class AppThemeSetting {
    SYSTEM,
    LIGHT,
    DARK
}

enum class RoundingSetting(val displayName: String, val roundingMode: RoundingMode) {
    HALF_UP("Half Up", RoundingMode.HALF_UP),
    HALF_DOWN("Half Down", RoundingMode.HALF_DOWN),
    HALF_EVEN("Half Even", RoundingMode.HALF_EVEN),
    UP("Up", RoundingMode.UP),
    DOWN("Down", RoundingMode.DOWN),
    CEILING("Ceiling", RoundingMode.CEILING),
    FLOOR("Floor", RoundingMode.FLOOR)
}

data class UserSettings(
    val theme: AppThemeSetting = AppThemeSetting.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val decimalPlaces: Int = 10,
    val roundingMode: RoundingSetting = RoundingSetting.HALF_UP
)
