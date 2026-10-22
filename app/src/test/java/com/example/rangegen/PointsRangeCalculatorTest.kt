package com.example.rangegen

import com.example.rangegen.domain.math.PointsRangeCalculator
import com.example.rangegen.domain.math.RangeFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class PointsRangeCalculatorTest {

    @Test
    fun criticalTest_basicByPoints() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            points = 6,
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

        assertEquals(0, BigDecimal("2").compareTo(res.interval))
        assertEquals(0, BigDecimal("30").compareTo(res.sum))
        assertEquals(0, BigDecimal("5").compareTo(res.average))
        assertEquals(0, BigDecimal("0").compareTo(res.minimum))
        assertEquals(0, BigDecimal("10").compareTo(res.maximum))
    }

    @Test
    fun criticalTest_byPointsExcludeEnd() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("10"),
            points = 5,
            includeEnd = false
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("0"),
            BigDecimal("2"),
            BigDecimal("4"),
            BigDecimal("6"),
            BigDecimal("8")
        )

        assertEquals(5, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }

        // End: 10 must not appear
        assertFalse(res.values.any { it.compareTo(BigDecimal("10")) == 0 })
    }

    @Test
    fun criticalTest_decimalRange() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("1"),
            points = 5,
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("0"),
            BigDecimal("0.25"),
            BigDecimal("0.5"),
            BigDecimal("0.75"),
            BigDecimal("1")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }

        val res2 = PointsRangeCalculator.generate(
            start = BigDecimal("1.5"),
            end = BigDecimal("3.5"),
            points = 5,
            includeEnd = true
        ).getOrThrow()

        val expected2 = listOf(
            BigDecimal("1.5"),
            BigDecimal("2"),
            BigDecimal("2.5"),
            BigDecimal("3"),
            BigDecimal("3.5")
        )

        assertEquals(expected2.size, res2.values.size)
        for (i in expected2.indices) {
            assertEquals(0, expected2[i].compareTo(res2.values[i]))
        }
    }

    @Test
    fun criticalTest_highPrecisionRange() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("1"),
            points = 7,
            includeEnd = true
        ).getOrThrow()

        assertEquals(7, res.values.size)
        assertEquals(0, BigDecimal("0").compareTo(res.values[0]))
        assertEquals(0, BigDecimal("1").compareTo(res.values[6]))

        // Format to 10 decimal places with HALF_UP
        val formatted = res.values.map {
            RangeFormatter.formatDecimal(it, 10, RoundingMode.HALF_UP)
        }

        assertEquals("0", formatted[0])
        assertEquals("0.1666666667", formatted[1])
        assertEquals("0.3333333333", formatted[2])
        assertEquals("0.5", formatted[3])
        assertEquals("0.6666666667", formatted[4])
        assertEquals("0.8333333333", formatted[5])
        assertEquals("1", formatted[6])
    }

    @Test
    fun criticalTest_negativeRange() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("-10"),
            end = BigDecimal("-2"),
            points = 5,
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("-10"),
            BigDecimal("-8"),
            BigDecimal("-6"),
            BigDecimal("-4"),
            BigDecimal("-2")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }
    }

    @Test
    fun criticalTest_crossZero() {
        val res1 = PointsRangeCalculator.generate(
            start = BigDecimal("-5"),
            end = BigDecimal("5"),
            points = 6,
            includeEnd = true
        ).getOrThrow()

        val expected1 = listOf(
            BigDecimal("-5"),
            BigDecimal("-3"),
            BigDecimal("-1"),
            BigDecimal("1"),
            BigDecimal("3"),
            BigDecimal("5")
        )
        for (i in expected1.indices) {
            assertEquals(0, expected1[i].compareTo(res1.values[i]))
        }

        val res2 = PointsRangeCalculator.generate(
            start = BigDecimal("-1"),
            end = BigDecimal("1"),
            points = 5,
            includeEnd = true
        ).getOrThrow()

        val expected2 = listOf(
            BigDecimal("-1"),
            BigDecimal("-0.5"),
            BigDecimal("0"),
            BigDecimal("0.5"),
            BigDecimal("1")
        )
        for (i in expected2.indices) {
            assertEquals(0, expected2[i].compareTo(res2.values[i]))
        }
    }

    @Test
    fun criticalTest_descendingRange() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("10"),
            end = BigDecimal("0"),
            points = 6,
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

        assertEquals(0, BigDecimal("-2").compareTo(res.interval))
        assertEquals(0, BigDecimal("0").compareTo(res.minimum))
        assertEquals(0, BigDecimal("10").compareTo(res.maximum))
    }

    @Test
    fun criticalTest_equalEndpoints() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("5"),
            end = BigDecimal("5"),
            points = 4,
            includeEnd = true
        ).getOrThrow()

        assertEquals(4, res.values.size)
        assertTrue(res.values.all { it.compareTo(BigDecimal("5")) == 0 })
        assertEquals(0, BigDecimal.ZERO.compareTo(res.interval))
        assertTrue(res.isConstantRange)
    }

    @Test
    fun criticalTest_noFloatingPointDrift() {
        val res = PointsRangeCalculator.generate(
            start = BigDecimal("0"),
            end = BigDecimal("1"),
            points = 11,
            includeEnd = true
        ).getOrThrow()

        val formatted = res.values.map {
            RangeFormatter.formatDecimal(it, 10, RoundingMode.HALF_UP)
        }

        val expected = listOf(
            "0", "0.1", "0.2", "0.3", "0.4", "0.5",
            "0.6", "0.7", "0.8", "0.9", "1"
        )

        assertEquals(expected, formatted)
        // Ensure never 0.30000000000000004
        assertFalse(formatted.contains("0.30000000000000004"))
    }

    @Test
    fun criticalTest_largeIntegerRange() {
        val start = BigDecimal("100000000000000000000")
        val end = BigDecimal("100000000000000000010")

        val res = PointsRangeCalculator.generate(
            start = start,
            end = end,
            points = 6,
            includeEnd = true
        ).getOrThrow()

        val expected = listOf(
            BigDecimal("100000000000000000000"),
            BigDecimal("100000000000000000002"),
            BigDecimal("100000000000000000004"),
            BigDecimal("100000000000000000006"),
            BigDecimal("100000000000000000008"),
            BigDecimal("100000000000000000010")
        )

        assertEquals(expected.size, res.values.size)
        for (i in expected.indices) {
            assertEquals(0, expected[i].compareTo(res.values[i]))
        }
    }
}
