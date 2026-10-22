package com.example.rangegen

import com.example.rangegen.domain.math.DirectValueCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.math.BigDecimal

class DirectValueCalculatorTest {

    @Test
    fun criticalTest_directIndex() {
        val start = BigDecimal("0")
        val interval = BigDecimal("2")

        // Index 1 = 0 + (1-1)*2 = 0
        assertEquals(0, BigDecimal("0").compareTo(
            DirectValueCalculator.calculateValueAtIndex(start, interval, 1)
        ))

        // Index 4 = 0 + (4-1)*2 = 6
        assertEquals(0, BigDecimal("6").compareTo(
            DirectValueCalculator.calculateValueAtIndex(start, interval, 4)
        ))

        // Index 6 = 0 + (6-1)*2 = 10
        assertEquals(0, BigDecimal("10").compareTo(
            DirectValueCalculator.calculateValueAtIndex(start, interval, 6)
        ))

        // High index: 100,000 -> 0 + 99999 * 2 = 199998
        assertEquals(0, BigDecimal("199998").compareTo(
            DirectValueCalculator.calculateValueAtIndex(start, interval, 100_000)
        ))
    }

    @Test
    fun directIndex_boundsValidation() {
        val start = BigDecimal("0")
        val interval = BigDecimal("1")

        try {
            DirectValueCalculator.calculateValueAtIndex(start, interval, 0)
            fail("Expected exception for index < 1")
        } catch (e: IllegalArgumentException) {
            assertEquals("Index must be at least 1.", e.message)
        }

        try {
            DirectValueCalculator.calculateValueAtIndex(start, interval, 100_001)
            fail("Expected exception for index > 100,000")
        } catch (e: IllegalArgumentException) {
            assertEquals("Index is too large. Maximum is 100,000.", e.message)
        }
    }
}
