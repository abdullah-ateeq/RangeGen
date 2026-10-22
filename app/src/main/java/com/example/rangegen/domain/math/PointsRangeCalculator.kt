package com.example.rangegen.domain.math

import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RangeResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object PointsRangeCalculator {

    val DEFAULT_MATH_CONTEXT = MathContext(80, RoundingMode.HALF_UP)

    fun generate(
        start: BigDecimal,
        end: BigDecimal,
        points: Int,
        includeEnd: Boolean,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): Result<RangeResult> {
        if (points < 2 || points > 1000) {
            return Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
        }

        // Equal start and end
        if (start.compareTo(end) == 0) {
            val values = List(points) { start }
            val zeroInterval = BigDecimal.ZERO
            val sum = start.multiply(BigDecimal(points), mathContext)
            return Result.success(
                RangeResult(
                    mode = RangeMode.BY_POINTS,
                    start = start,
                    end = end,
                    interval = zeroInterval,
                    values = values,
                    sum = sum,
                    average = start,
                    minimum = start,
                    maximum = start,
                    endReached = true,
                    includeEnd = includeEnd,
                    isConstantRange = true,
                    requestedPointsOrStep = points.toString()
                )
            )
        }

        val step: BigDecimal
        val values = ArrayList<BigDecimal>(points)

        if (includeEnd) {
            // step = (end - start) / (points - 1)
            step = end.subtract(start, mathContext).divide(BigDecimal(points - 1), mathContext)
            for (i in 0 until points) {
                val value = when (i) {
                    0 -> start
                    points - 1 -> end
                    else -> start.add(step.multiply(BigDecimal(i), mathContext), mathContext)
                }
                values.add(value)
            }
        } else {
            // step = (end - start) / points
            step = end.subtract(start, mathContext).divide(BigDecimal(points), mathContext)
            for (i in 0 until points) {
                val value = if (i == 0) {
                    start
                } else {
                    start.add(step.multiply(BigDecimal(i), mathContext), mathContext)
                }
                values.add(value)
            }
        }

        val min = values.minOrNull() ?: start
        val max = values.maxOrNull() ?: end
        val sum = values.fold(BigDecimal.ZERO) { acc, v -> acc.add(v, mathContext) }
        val average = sum.divide(BigDecimal(values.size), mathContext)

        return Result.success(
            RangeResult(
                mode = RangeMode.BY_POINTS,
                start = start,
                end = end,
                interval = step,
                values = values,
                sum = sum,
                average = average,
                minimum = min,
                maximum = max,
                endReached = includeEnd,
                includeEnd = includeEnd,
                isConstantRange = false,
                requestedPointsOrStep = points.toString()
            )
        )
    }
}
