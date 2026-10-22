package com.example.rangegen

import com.example.rangegen.data.model.RangeMode
import com.example.rangegen.domain.math.RangeValidator
import com.example.rangegen.domain.math.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RangeValidatorTest {

    @Test
    fun validate_emptyInputs() {
        val emptyStart = RangeValidator.validate(RangeMode.BY_POINTS, "", "10", "6")
        assertTrue(emptyStart is ValidationResult.Error)
        assertEquals("Enter a start value.", (emptyStart as ValidationResult.Error).message)

        val emptyEnd = RangeValidator.validate(RangeMode.BY_POINTS, "0", "", "6")
        assertTrue(emptyEnd is ValidationResult.Error)
        assertEquals("Enter an end value.", (emptyEnd as ValidationResult.Error).message)

        val emptyPoints = RangeValidator.validate(RangeMode.BY_POINTS, "0", "10", "")
        assertTrue(emptyPoints is ValidationResult.Error)
        assertEquals("Enter a number of points.", (emptyPoints as ValidationResult.Error).message)

        val emptyStep = RangeValidator.validate(RangeMode.BY_STEP, "0", "10", "")
        assertTrue(emptyStep is ValidationResult.Error)
        assertEquals("Enter a step.", (emptyStep as ValidationResult.Error).message)
    }

    @Test
    fun validate_zeroStep() {
        val res = RangeValidator.validate(RangeMode.BY_STEP, "0", "10", "0")
        assertTrue(res is ValidationResult.Error)
        assertEquals("Step cannot be zero.", (res as ValidationResult.Error).message)
    }

    @Test
    fun validate_equalEndpointsByStep() {
        val res = RangeValidator.validate(RangeMode.BY_STEP, "5", "5", "1")
        assertTrue(res is ValidationResult.Error)
        assertEquals(
            "Start and end are equal. Use By Points to repeat the value, or choose different endpoints.",
            (res as ValidationResult.Error).message
        )
    }

    @Test
    fun validate_stepDirection() {
        // Ascending with negative step
        val ascWithNegStep = RangeValidator.validate(RangeMode.BY_STEP, "0", "10", "-2")
        assertTrue(ascWithNegStep is ValidationResult.Error)
        assertEquals(
            "Use a positive step when the end value is greater than the start value.",
            (ascWithNegStep as ValidationResult.Error).message
        )

        // Descending with positive step
        val descWithPosStep = RangeValidator.validate(RangeMode.BY_STEP, "10", "0", "2")
        assertTrue(descWithPosStep is ValidationResult.Error)
        assertEquals(
            "Use a negative step when the end value is less than the start value.",
            (descWithPosStep as ValidationResult.Error).message
        )

        // Valid directions
        val validAsc = RangeValidator.validate(RangeMode.BY_STEP, "0", "10", "2")
        assertTrue(validAsc is ValidationResult.SuccessByStep)

        val validDesc = RangeValidator.validate(RangeMode.BY_STEP, "10", "0", "-2")
        assertTrue(validDesc is ValidationResult.SuccessByStep)
    }

    @Test
    fun validate_stepOutputLimitProtection() {
        // Step would generate > 1000 values
        val tooMany = RangeValidator.validate(RangeMode.BY_STEP, "0", "10000", "1")
        assertTrue(tooMany is ValidationResult.Error)
        assertEquals(
            "This range would generate too many values. Increase the step or reduce the range.",
            (tooMany as ValidationResult.Error).message
        )

        val safe = RangeValidator.validate(RangeMode.BY_STEP, "0", "100", "0.2")
        assertTrue(safe is ValidationResult.SuccessByStep)
    }
}
