package com.example.rangegen.domain.math

import java.math.BigDecimal
import java.math.MathContext

object DirectValueCalculator {

    fun calculateValueAtIndex(
        start: BigDecimal,
        interval: BigDecimal,
        index: Int,
        mathContext: MathContext = PointsRangeCalculator.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        require(index >= 1) { "Index must be at least 1." }
        require(index <= 100_000) { "Index is too large. Maximum is 100,000." }

        // value = start + (index - 1) * interval
        val multiplier = BigDecimal(index - 1)
        return start.add(interval.multiply(multiplier, mathContext), mathContext)
    }
}
