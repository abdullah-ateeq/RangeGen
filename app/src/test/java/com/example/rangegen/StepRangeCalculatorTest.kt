package com.example.rangegen

import com.example.rangegen.domain.math.StepRangeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class StepRangeCalculatorTest {

    @Test
    fun criticalTest_basicByStep() {
        val res = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("2"),
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("0"),
            BigDecimal("2"),
            BigDecimal("4"),
            BigDecimal("6"),
            BigDecimal("8"),
            BigDecimal("10")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }
        assertTrue(res.endReached)
    }

    @Test
    fun criticalTest_stepDoesNotReachEnd() {
        val res = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("3"),
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("0"),
            BigDecimal("3"),
            BigDecimal("6"),
            BigDecimal("9")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }
        assertFalse(res.endReached)
        // 10 must not be included
        assertFalse(res.values.any { it.compareTo(BigDecimal("10")) == 0 })
        assertEquals(0, BigDecimal("9").compareTo(res.values.last()))
    }

    @Test
    fun criticalTest_byStepExcludeEnd() {
        // When step does not land on end
        val res1 = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("3"),
            includeEnd = false
        ).getOrThrow()

        val expected1 = listOf(
            BigDecimal("0"),
            BigDecimal("3"),
            BigDecimal("6"),
            BigDecimal("9")
        )
        assertEquals(expected1.size, res1.values.size)
        for (i in expected1.indices) {
            assertEquals(0, expected1[i].compareTo(res1.values[i]))
        }

        // When step WOULD land on end, but includeEnd is false
        val res2 = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("2"),
            includeEnd = false
        ).getOrThrow()

        val expected2 = listOf(
            BigDecimal("0"),
            BigDecimal("2"),
            BigDecimal("4"),
            BigDecimal("6"),
            BigDecimal("8")
        )
        assertEquals(expected2.size, res2.values.size)
        for (i in expected2.indices) {
            assertEquals(0, expected2[i].compareTo(res2.values[i]))
        }
        assertFalse(res2.values.any { it.compareTo(BigDecimal("10")) == 0 })
    }

    @Test
    fun criticalTest_descendingByStep() {
        val res = StepRangeCalculator.generate(
            start = BigDecimal("10"),
            end = BigDecimal("0"),
            step = BigDecimal("-2"),
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("10"),
            BigDecimal("8"),
            BigDecimal("6"),
            BigDecimal("4"),
            BigDecimal("2"),
            BigDecimal("0")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }
        assertTrue(res.endReached)
        assertEquals(0, BigDecimal("0").compareTo(res.minimum))
        assertEquals(0, BigDecimal("10").compareTo(res.maximum))
    }

    @Test
    fun step_rejections() {
        // Zero step
        val zeroStep = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("0"),
            includeEnd = true
        )
        assertTrue(zeroStep.isFailure)
        assertEquals("Step cannot be zero.", zeroStep.exceptionOrNull()?.message)

        // Equal start and end
        val equalEndpoints = StepRangeCalculator.generate(
            start = BigDecimal("5"),
            end = BigDecimal("5"),
            step = BigDecimal("1"),
            includeEnd = true
        )
        assertTrue(equalEndpoints.isFailure)
        assertEquals(
            "Start and end are equal. Use By Points to repeat the value, or choose different endpoints.",
            equalEndpoints.exceptionOrNull()?.message
        )

        // Direction error ascending
        val dirAsc = StepRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            step = BigDecimal("-2"),
            includeEnd = true
        )
        assertTrue(dirAsc.isFailure)
        assertEquals(
            "Use a positive step when the end value is greater than the start value.",
            dirAsc.exceptionOrNull()?.message
        )

        // Direction error descending
        val dirDesc = StepRangeCalculator.generate(
            start = BigDecimal("10"),
            end = BigDecimal("0"),
            step = BigDecimal("2"),
            includeEnd = true
        )
        assertTrue(dirDesc.isFailure)
        assertEquals(
            "Use a negative step when the end value is less than the start value.",
            dirDesc.exceptionOrNull()?.message
        )
    }
}
