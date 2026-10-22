package com.example.rangegen.domain.math

import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RangeResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object StepRangeCalculator {

    val DEFAULT_MATH_CONTEXT = MathContext(80, RoundingMode.HALF_UP)
    private const val MAX_OUTPUT_LIMIT = 1000

    fun generate(
        start: BigDecimal,
        end: BigDecimal,
        step: BigDecimal,
        includeEnd: Boolean,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): Result<RangeResult> {
        if (step.compareTo(BigDecimal.ZERO) == 0) {
            return Result.failure(IllegalArgumentException("Step cannot be zero."))
        }

        if (start.compareTo(end) == 0) {
            return Result.failure(IllegalArgumentException("Start and end are equal. Use By Points to repeat the value, or choose different endpoints."))
        }

        val isAscending = step > BigDecimal.ZERO
        if (end > start && !isAscending) {
            return Result.failure(IllegalArgumentException("Use a positive step when the end value is greater than the start value."))
        }
        if (end < start && isAscending) {
            return Result.failure(IllegalArgumentException("Use a negative step when the end value is less than the start value."))
        }

        // Check if exact end reachability
        val diff = end.subtract(start, mathContext)
        val remainder = diff.remainder(step, mathContext)
        val isExactEnd = remainder.compareTo(BigDecimal.ZERO) == 0

        val values = mutableListOf<BigDecimal>()
        var currentIndex = 0

        while (true) {
            if (values.size >= MAX_OUTPUT_LIMIT) {
                return Result.failure(IllegalStateException("This range would generate too many values. Increase the step or reduce the range."))
            }

            val currentVal = start.add(step.multiply(BigDecimal(currentIndex), mathContext), mathContext)
            val comp = if (isAscending) currentVal.compareTo(end) else end.compareTo(currentVal)

            if (comp > 0) {
                // Moved beyond end
                break
            } else if (comp == 0) {
                // Landed exactly on end
                if (includeEnd) {
                    values.add(currentVal)
                }
                break
            } else {
                // Strictly before end
                values.add(currentVal)
            }

            currentIndex++
        }

        if (values.isEmpty()) {
            // Edge case e.g. start == end and excludeEnd
            values.add(start)
        }

        val min = values.minOrNull() ?: start
        val max = values.maxOrNull() ?: end
        val sum = values.fold(BigDecimal.ZERO) { acc, v -> acc.add(v, mathContext) }
        val average = sum.divide(BigDecimal(values.size), mathContext)
        val endReached = isExactEnd && includeEnd && (values.last().compareTo(end) == 0)

        return Result.success(
            RangeResult(
                mode = RangeMode.BY_STEP,
                start = start,
                end = end,
                interval = step,
                values = values,
                sum = sum,
                average = average,
                minimum = min,
                maximum = max,
                endReached = endReached,
                includeEnd = includeEnd,
                isConstantRange = false,
                requestedPointsOrStep = step.toPlainString()
            )
        )
    }
}
