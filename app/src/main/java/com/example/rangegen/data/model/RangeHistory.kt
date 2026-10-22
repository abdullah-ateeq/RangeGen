package com.example.rangegen.data.model

data class RangeHistory(
    val id: String,
    val mode: RangeMode,
    val start: String,
    val end: String,
    val pointsOrStep: String,
    val includeEnd: Boolean,
    val savedAt: Long
)
