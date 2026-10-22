package com.example.rangegen

import com.example.rangegen.domain.math.RangeGenParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class RangeGenParserTest {

    @Test
    fun parseDecimal_validInputs() {
        val cases = mapOf(
            "0" to BigDecimal("0"),
            "10" to BigDecimal("10"),
            "-10" to BigDecimal("-10"),
            "+100" to BigDecimal("100"),
            "1.5" to BigDecimal("1.5"),
            "-2.75" to BigDecimal("-2.75"),
            "0.125" to BigDecimal("0.125"),
            " 25 " to BigDecimal("25"),
            "100000000000000000000" to BigDecimal("100000000000000000000")
        )

        for ((input, expected) in cases) {
            val res = RangeGenParser.parseDecimal(input)
            assertTrue("Expected success for '$input'", res.isSuccess)
            assertEquals(0, expected.compareTo(res.getOrThrow()))
        }
    }

    @Test
    fun parseDecimal_rejectsInvalidFormats() {
        val invalidInputs = listOf(
            "",
            "   ",
            "abc",
            "1.2.3",
            "--5",
            "++5",
            "1 000",
            "1E5",
            "2.5e3"
        )

        for (input in invalidInputs) {
            val res = RangeGenParser.parseDecimal(input)
            assertFalse("Expected failure for '$input'", res.isSuccess)
        }
    }

    @Test
    fun parseDecimal_rejectsInputLongerThan500Characters() {
        val longInput = "1" + "0".repeat(505)
        val res = RangeGenParser.parseDecimal(longInput)
        assertTrue(res.isFailure)
        assertEquals("Number is too long. Maximum 500 characters.", res.exceptionOrNull()?.message)
    }

    @Test
    fun parsePositiveInteger_validRange() {
        val valid = listOf("2", "6", "10", "100", "1000", " +50 ")
        for (input in valid) {
            val res = RangeGenParser.parsePositiveInteger(input)
            assertTrue("Expected success for '$input'", res.isSuccess)
        }
        assertEquals(2, RangeGenParser.parsePositiveInteger("2").getOrThrow())
        assertEquals(1000, RangeGenParser.parsePositiveInteger("1000").getOrThrow())
    }

    @Test
    fun parsePositiveInteger_rejectsOutOfRangeAndInvalid() {
        val invalid = listOf("0", "1", "-1", "1.5", "1001", "abc", "", "1 000")
        for (input in invalid) {
            val res = RangeGenParser.parsePositiveInteger(input)
            assertTrue("Expected failure for '$input'", res.isFailure)
            assertEquals("Enter a whole number from 2 to 1000.", res.exceptionOrNull()?.message)
        }
    }

    @Test
    fun parseLookupIndex_validAndInvalid() {
        assertTrue(RangeGenParser.parseLookupIndex("1").isSuccess)
        assertEquals(1, RangeGenParser.parseLookupIndex("1").getOrThrow())
        assertEquals(100_000, RangeGenParser.parseLookupIndex("100000").getOrThrow())

        // Out of range
        val tooLarge = RangeGenParser.parseLookupIndex("100001")
        assertTrue(tooLarge.isFailure)
        assertEquals("Index is too large. Maximum is 100,000.", tooLarge.exceptionOrNull()?.message)

        val tooSmall = RangeGenParser.parseLookupIndex("0")
        assertTrue(tooSmall.isFailure)
        assertEquals("Index must be at least 1.", tooSmall.exceptionOrNull()?.message)
    }
}
