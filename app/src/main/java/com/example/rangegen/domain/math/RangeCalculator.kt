package com.example.rangegen.domain.math

import com.example.rangegen.data.model.RangeResult
import java.math.BigDecimal
import java.math.MathContext

object RangeCalculator {

    fun generateByPoints(
        start: BigDecimal,
        end: BigDecimal,
        points: Int,
        includeEnd: Boolean,
        mathContext: MathContext = PointsRangeCalculator.DEFAULT_MATH_CONTEXT
    ): Result<RangeResult> {
        return PointsRangeCalculator.generate(start, end, points, includeEnd, mathContext)
    }

    fun generateByStep(
        start: BigDecimal,
        end: BigDecimal,
        step: BigDecimal,
        includeEnd: Boolean,
        mathContext: MathContext = StepRangeCalculator.DEFAULT_MATH_CONTEXT
    ): Result<RangeResult> {
        return StepRangeCalculator.generate(start, end, step, includeEnd, mathContext)
    }
}
