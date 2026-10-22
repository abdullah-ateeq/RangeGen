package com.example.rangegen.data.model

import java.math.BigDecimal

data class RangeResult(
    val mode: RangeMode,
    val start: BigDecimal,
    val end: BigDecimal,
    val interval: BigDecimal,
    val values: List<BigDecimal>,
    val sum: BigDecimal,
    val average: BigDecimal,
    val minimum: BigDecimal,
    val maximum: BigDecimal,
    val endReached: Boolean,
    val includeEnd: Boolean,
    val calculatedAt: Long = System.currentTimeMillis(),
    val isConstantRange: Boolean = false,
    val requestedPointsOrStep: String = ""
)
