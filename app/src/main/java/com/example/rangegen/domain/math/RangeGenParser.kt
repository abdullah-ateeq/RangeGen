package com.example.rangegen.domain.math

import java.math.BigDecimal

object RangeGenParser {

    private const val MAX_INPUT_LENGTH = 500

    /**
     * Parses a decimal string into a BigDecimal.
     * Trims surrounding whitespace.
     * Rejects:
     * - Strings longer than 500 characters
     * - Blank strings
     * - Strings with internal spaces
     * - Scientific notation (e or E)
     * - Multiple decimal points or signs
     * - Non-numeric characters
     */
    fun parseDecimal(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Input cannot be empty."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 characters."))
        }
        if (trimmed.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException("Internal spaces are not allowed."))
        }
        if (trimmed.contains('e', ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Scientific notation is not supported."))
        }

        // Strict decimal regex: optional single + or -, followed by digits, optional single dot and digits
        val decimalRegex = Regex("^[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)$")
        if (!decimalRegex.matches(trimmed)) {
            return Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }

        return try {
            val bd = BigDecimal(trimmed)
            Result.success(bd)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Enter a valid decimal number."))
        }
    }

    /**
     * Parses a positive integer string for point counts (2 to 1000).
     */
    fun parsePositiveInteger(input: String): Result<Int> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Number is too long. Maximum 500 characters."))
        }
        if (trimmed.any { it.isWhitespace() }) {
            return Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
        }

        // Only allow optional '+' and digits
        val intRegex = Regex("^[+]?\\d+$")
        if (!intRegex.matches(trimmed)) {
            return Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
        }

        return try {
            val value = trimmed.toInt()
            if (value in 2..1000) {
                Result.success(value)
            } else {
                Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Enter a whole number from 2 to 1000."))
        }
    }

    /**
     * Parses a positive lookup index (1 to 100,000).
     */
    fun parseLookupIndex(input: String): Result<Int> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter an index."))
        }
        val intRegex = Regex("^[+]?\\d+$")
        if (!intRegex.matches(trimmed)) {
            return Result.failure(IllegalArgumentException("Enter a valid whole number index."))
        }
        return try {
            val value = trimmed.toLong()
            if (value > 100_000L) {
                Result.failure(IllegalArgumentException("Index is too large. Maximum is 100,000."))
            } else if (value < 1L) {
                Result.failure(IllegalArgumentException("Index must be at least 1."))
            } else {
                Result.success(value.toInt())
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Index is too large. Maximum is 100,000."))
        }
    }
}
