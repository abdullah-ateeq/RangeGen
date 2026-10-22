package com.example.rangegen.domain.math

import java.math.BigDecimal

data class CalculationStep(
    val index: Int,
    val formulaDescription: String,
    val calculatedValue: BigDecimal
)
