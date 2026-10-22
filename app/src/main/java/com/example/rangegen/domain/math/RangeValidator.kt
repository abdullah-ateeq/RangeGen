package com.example.rangegen.domain.math

import com.example.rangegen.data.model.RangeMode
import java.math.BigDecimal
import java.math.BigInteger

sealed class ValidationResult {
    data class SuccessByPoints(
        val start: BigDecimal,
        val end: BigDecimal,
        val points: Int
    ) : ValidationResult()

    data class SuccessByStep(
        val start: BigDecimal,
        val end: BigDecimal,
        val step: BigDecimal
    ) : ValidationResult()

    data class Error(val message: String) : ValidationResult()
}

object RangeValidator {

    private const val MAX_OUTPUT_LIMIT = 1000

    fun validate(
        mode: RangeMode,
        startRaw: String,
        endRaw: String,
        pointsOrStepRaw: String
    ): ValidationResult {
        // 1. Check empty inputs
        if (startRaw.trim().isEmpty()) {
            return ValidationResult.Error("Enter a start value.")
        }
        if (endRaw.trim().isEmpty()) {
            return ValidationResult.Error("Enter an end value.")
        }

        // 2. Parse Start and End
        val startParsed = RangeGenParser.parseDecimal(startRaw)
        if (startParsed.isFailure) {
            val err = startParsed.exceptionOrNull()?.message ?: "Enter a valid decimal number."
            return ValidationResult.Error(err)
        }
        val start = startParsed.getOrThrow()

        val endParsed = RangeGenParser.parseDecimal(endRaw)
        if (endParsed.isFailure) {
            val err = endParsed.exceptionOrNull()?.message ?: "Enter a valid decimal number."
            return ValidationResult.Error(err)
        }
        val end = endParsed.getOrThrow()

        return when (mode) {
            RangeMode.BY_POINTS -> {
                if (pointsOrStepRaw.trim().isEmpty()) {
                    return ValidationResult.Error("Enter a number of points.")
                }
                val pointsParsed = RangeGenParser.parsePositiveInteger(pointsOrStepRaw)
                if (pointsParsed.isFailure) {
                    val err = pointsParsed.exceptionOrNull()?.message ?: "Enter a whole number from 2 to 1000."
                    return ValidationResult.Error(err)
                }
                val points = pointsParsed.getOrThrow()
                ValidationResult.SuccessByPoints(start, end, points)
            }

            RangeMode.BY_STEP -> {
                if (pointsOrStepRaw.trim().isEmpty()) {
                    return ValidationResult.Error("Enter a step.")
                }
                val stepParsed = RangeGenParser.parseDecimal(pointsOrStepRaw)
                if (stepParsed.isFailure) {
                    val err = stepParsed.exceptionOrNull()?.message ?: "Enter a valid decimal number."
                    return ValidationResult.Error(err)
                }
                val step = stepParsed.getOrThrow()

                // Step cannot be zero
                if (step.compareTo(BigDecimal.ZERO) == 0) {
                    return ValidationResult.Error("Step cannot be zero.")
                }

                // Start and end equal check
                if (start.compareTo(end) == 0) {
                    return ValidationResult.Error("Start and end are equal. Use By Points to repeat the value, or choose different endpoints.")
                }

                // Step direction check
                if (end > start && step < BigDecimal.ZERO) {
                    return ValidationResult.Error("Use a positive step when the end value is greater than the start value.")
                }
                if (end < start && step > BigDecimal.ZERO) {
                    return ValidationResult.Error("Use a negative step when the end value is less than the start value.")
                }

                // Check output limit
                val diff = (end - start).abs()
                val stepAbs = step.abs()
                // diff / stepAbs gives the number of intervals
                val estimatedIntervals = diff.divideToIntegralValue(stepAbs)
                val estimatedCount = estimatedIntervals.toBigInteger() + BigInteger.ONE
                if (estimatedCount > BigInteger.valueOf(MAX_OUTPUT_LIMIT.toLong())) {
                    return ValidationResult.Error("This range would generate too many values. Increase the step or reduce the range.")
                }

                ValidationResult.SuccessByStep(start, end, step)
            }
        }
    }
}
