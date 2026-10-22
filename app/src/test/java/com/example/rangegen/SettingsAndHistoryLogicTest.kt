package com.example.rangegen

import com.example.rangegen.data.model.AppThemeSetting
import com.example.rangegen.data.model.RangeHistory
import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RoundingSetting
import com.example.rangegen.data.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsAndHistoryLogicTest {

    @Test
    fun history_fifoLimitLogic() {
        val maxItems = 10
        val historyList = mutableListOf<RangeHistory>()

        // Add 15 items
        for (i in 1..15) {
            val item = RangeHistory(
                id = "$i",
                mode = RangeMode.BY_POINTS,
                start = "0",
                end = "$i",
                pointsOrStep = "5",
                includeEnd = true,
                savedAt = i.toLong()
            )
            historyList.add(0, item)
            while (historyList.size > maxItems) {
                historyList.removeAt(historyList.lastIndex)
            }
        }

        // Must cap at 10
        assertEquals(10, historyList.size)
        // Most recent is 15
        assertEquals("15", historyList[0].id)
        // Oldest retained is 6 (15 - 10 + 1)
        assertEquals("6", historyList.last().id)
    }

    @Test
    fun history_consecutiveDuplicatePrevention() {
        val historyList = mutableListOf<RangeHistory>()

        fun tryAdd(item: RangeHistory): Boolean {
            if (historyList.isNotEmpty()) {
                val latest = historyList[0]
                if (latest.mode == item.mode &&
                    latest.start == item.start &&
                    latest.end == item.end &&
                    latest.pointsOrStep == item.pointsOrStep &&
                    latest.includeEnd == item.includeEnd
                ) {
                    return false
                }
            }
            historyList.add(0, item)
            return true
        }

        val item1 = RangeHistory("1", RangeMode.BY_POINTS, "0", "10", "6", true, 100L)
        val item1Duplicate = RangeHistory("2", RangeMode.BY_POINTS, "0", "10", "6", true, 200L)
        val item2 = RangeHistory("3", RangeMode.BY_STEP, "0", "10", "2", true, 300L)

        assertTrue(tryAdd(item1))
        // Consecutive duplicate is rejected
        assertFalse(tryAdd(item1Duplicate))
        assertEquals(1, historyList.size)

        // Adding different item is accepted
        assertTrue(tryAdd(item2))
        assertEquals(2, historyList.size)
    }

    @Test
    fun settings_defaultValuesAndReset() {
        val defaultSettings = UserSettings()

        assertEquals(AppThemeSetting.SYSTEM, defaultSettings.theme)
        assertTrue(defaultSettings.dynamicColor)
        assertTrue(defaultSettings.hapticFeedback)
        assertTrue(defaultSettings.animations)
        assertTrue(defaultSettings.confirmDelete)
        assertEquals(10, defaultSettings.decimalPlaces)
        assertEquals(RoundingSetting.HALF_UP, defaultSettings.roundingMode)

        // Modified settings
        val modified = defaultSettings.copy(
            theme = AppThemeSetting.DARK,
            decimalPlaces = 4,
            roundingMode = RoundingSetting.HALF_EVEN
        )

        assertEquals(AppThemeSetting.DARK, modified.theme)
        assertEquals(4, modified.decimalPlaces)
        assertEquals(RoundingSetting.HALF_EVEN, modified.roundingMode)

        // Reset
        val reset = UserSettings()
        assertEquals(AppThemeSetting.SYSTEM, reset.theme)
        assertEquals(10, reset.decimalPlaces)
        assertEquals(RoundingSetting.HALF_UP, reset.roundingMode)
    }
}
