package com.example.rangegen.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.rangegen.data.model.RangeHistory
import com.example.rangegen.data.model.RangeMode
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class HistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "rangegen_history"
        private const val KEY_HISTORY = "history_json"
        private const val MAX_HISTORY_ITEMS = 10
    }

    @Synchronized
    fun getHistory(): List<RangeHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<RangeHistory>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", UUID.randomUUID().toString())
                val mode = RangeMode.valueOf(obj.getString("mode"))
                val start = obj.getString("start")
                val end = obj.getString("end")
                val pointsOrStep = obj.getString("pointsOrStep")
                val includeEnd = obj.getBoolean("includeEnd")
                val savedAt = obj.getLong("savedAt")
                list.add(RangeHistory(id, mode, start, end, pointsOrStep, includeEnd, savedAt))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Saves a history item.
     * Prevents consecutive duplicates.
     * Keeps maximum 10 items (removes oldest when 11th is added).
     * Returns true if saved, false if skipped as consecutive duplicate.
     */
    @Synchronized
    fun addHistoryItem(
        mode: RangeMode,
        start: String,
        end: String,
        pointsOrStep: String,
        includeEnd: Boolean
    ): Boolean {
        val currentList = getHistory().toMutableList()

        // Check consecutive duplicate: compare with latest item (index 0)
        if (currentList.isNotEmpty()) {
            val latest = currentList[0]
            if (latest.mode == mode &&
                latest.start == start &&
                latest.end == end &&
                latest.pointsOrStep == pointsOrStep &&
                latest.includeEnd == includeEnd
            ) {
                return false
            }
        }

        val newItem = RangeHistory(
            id = UUID.randomUUID().toString(),
            mode = mode,
            start = start,
            end = end,
            pointsOrStep = pointsOrStep,
            includeEnd = includeEnd,
            savedAt = System.currentTimeMillis()
        )

        // Insert at beginning
        currentList.add(0, newItem)

        // Enforce max 10 entries (removes oldest items at the end)
        while (currentList.size > MAX_HISTORY_ITEMS) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveList(currentList)
        return true
    }

    @Synchronized
    fun deleteItem(id: String) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveList(currentList)
    }

    @Synchronized
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveList(list: List<RangeHistory>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("mode", item.mode.name)
                put("start", item.start)
                put("end", item.end)
                put("pointsOrStep", item.pointsOrStep)
                put("includeEnd", item.includeEnd)
                put("savedAt", item.savedAt)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}
