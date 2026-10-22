package com.example.rangegen

import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.data.model.RangeResult
import com.example.rangegen.domain.math.PointsRangeCalculator
import com.example.rangegen.domain.math.RangeFormatter
import com.example.rangegen.domain.math.StepRangeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class RangeFormatterTest {

    @Test
    fun formatDecimal_trailingZeroRemoval() {
        val half = BigDecimal("0.5000000000")
        assertEquals("0.5", RangeFormatter.formatDecimal(half, 10, RoundingMode.HALF_UP))

        val five = BigDecimal("5.0000000000")
        assertEquals("5", RangeFormatter.formatDecimal(five, 10, RoundingMode.HALF_UP))

        val zero = BigDecimal("0.0000000000")
        assertEquals("0", RangeFormatter.formatDecimal(zero, 10, RoundingMode.HALF_UP))
    }

    @Test
    fun formatDecimal_roundingModes() {
        val num = BigDecimal("2.55")

        assertEquals("2.6", RangeFormatter.formatDecimal(num, 1, RoundingMode.HALF_UP))
        assertEquals("2.5", RangeFormatter.formatDecimal(num, 1, RoundingMode.HALF_DOWN))
        assertEquals("2.6", RangeFormatter.formatDecimal(num, 1, RoundingMode.HALF_EVEN))
        assertEquals("2.6", RangeFormatter.formatDecimal(num, 1, RoundingMode.CEILING))
        assertEquals("2.5", RangeFormatter.formatDecimal(num, 1, RoundingMode.FLOOR))
        assertEquals("2.6", RangeFormatter.formatDecimal(num, 1, RoundingMode.UP))
        assertEquals("2.5", RangeFormatter.formatDecimal(num, 1, RoundingMode.DOWN))
    }

    @Test
    fun formatCopyShareText_byPoints() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            points = 6,
            includeEnd = true
        ).getOrThrow()

        val text = RangeFormatter.formatCopyShareText(res, 10, RoundingMode.HALF_UP)

        assertTrue(text.contains("RangeGen"))
        assertTrue(text.contains("Evenly Spaced Range"))
        assertTrue(text.contains("Start: 0"))
        assertTrue(text.contains("End: 10"))
        assertTrue(text.contains("Points: 6"))
        assertTrue(text.contains("Interval: 2"))
        assertTrue(text.contains("0, 2, 4, 6, 8, 10"))
        assertTrue(text.contains("Minimum: 0"))
        assertTrue(text.contains("Maximum: 10"))
        assertTrue(text.contains("Sum: 30"))
        assertTrue(text.contains("Average: 5"))
    }

    @Test
    fun formatCopyShareText_byStep() {
        val res = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("2"),
            includeEnd = true
        ).getOrThrow()

        val text = RangeFormatter.formatCopyShareText(res, 10, RoundingMode.HALF_UP)

        assertTrue(text.contains("RangeGen"))
        assertTrue(text.contains("Evenly Spaced Range"))
        assertTrue(text.contains("Start: 0"))
        assertTrue(text.contains("End: 10"))
        assertTrue(text.contains("Step: 2"))
        assertTrue(text.contains("Values: 6"))
        assertTrue(text.contains("End Reached: Yes"))
        assertTrue(text.contains("0, 2, 4, 6, 8, 10"))
    }

    @Test
    fun formatCopyShareText_byStepUnreachable() {
        val res = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("3"),
            includeEnd = true
        ).getOrThrow()

        val text = RangeFormatter.formatCopyShareText(res, 10, RoundingMode.HALF_UP)

        assertTrue(text.contains("End Reached: No"))
        assertTrue(text.contains("0, 3, 6, 9"))
    }
}
