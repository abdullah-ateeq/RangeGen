package com.example.rangegen.domain.math

import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RangeResult
import java.math.BigDecimal
import java.math.RoundingMode

object RangeFormatter {

    private const val MAX_DISPLAY_DIGITS = 2000

    fun formatDecimal(
        value: BigDecimal,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }
        val rounded = value.setScale(decimalPlaces, roundingMode)
        val stripped = rounded.stripTrailingZeros()
        val plain = if (stripped.scale() < 0) {
            stripped.setScale(0, roundingMode).toPlainString()
        } else {
            stripped.toPlainString()
        }
        if (plain.length > MAX_DISPLAY_DIGITS) {
            return plain.take(50) + "… [value too large to display in full]"
        }
        return plain
    }

    fun formatCopyShareText(
        result: RangeResult,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val sb = StringBuilder()
        sb.appendLine("RangeGen")
        sb.appendLine()
        sb.appendLine("Evenly Spaced Range")
        sb.appendLine()

        val startStr = formatDecimal(result.start, decimalPlaces, roundingMode)
        val endStr = formatDecimal(result.end, decimalPlaces, roundingMode)
        val minStr = formatDecimal(result.minimum, decimalPlaces, roundingMode)
        val maxStr = formatDecimal(result.maximum, decimalPlaces, roundingMode)
        val sumStr = formatDecimal(result.sum, decimalPlaces, roundingMode)
        val avgStr = formatDecimal(result.average, decimalPlaces, roundingMode)

        sb.appendLine("Start: $startStr")
        sb.appendLine("End: $endStr")

        when (result.mode) {
            RangeMode.BY_POINTS -> {
                sb.appendLine("Points: ${result.values.size}")
                val intervalStr = formatDecimal(result.interval, decimalPlaces, roundingMode)
                sb.appendLine("Interval: $intervalStr")
            }

            RangeMode.BY_STEP -> {
                val stepStr = formatDecimal(result.interval, decimalPlaces, roundingMode)
                sb.appendLine("Step: $stepStr")
                sb.appendLine("Values: ${result.values.size}")
                sb.appendLine("End Reached: ${if (result.endReached) "Yes" else "No"}")
            }
        }

        sb.appendLine()
        sb.appendLine("Values:")
        val valuesStr = result.values.joinToString(", ") {
            formatDecimal(it, decimalPlaces, roundingMode)
        }
        sb.appendLine(valuesStr)
        sb.appendLine()

        sb.appendLine("Minimum: $minStr")
        sb.appendLine("Maximum: $maxStr")
        sb.appendLine("Sum: $sumStr")
        sb.append("Average: $avgStr")

        return sb.toString()
    }
}
