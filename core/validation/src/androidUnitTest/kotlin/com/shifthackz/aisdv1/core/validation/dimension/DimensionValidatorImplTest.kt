package com.shifthackz.aisdv1.core.validation.dimension

import com.shifthackz.aisdv1.core.validation.ValidationResult
import org.junit.Assert
import org.junit.Test

class DimensionValidatorImplTest {

    private val validator = DimensionValidatorImpl(MIN, MAX)

    @Test
    fun `given input is null, expected not valid with Empty error`() {
        val expected = ValidationResult<DimensionValidator.Error>(
            isValid = false,
            validationError = DimensionValidator.Error.Empty,
        )
        val actual = validator(null)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is empty, expected not valid with Empty error`() {
        val expected = ValidationResult<DimensionValidator.Error>(
            isValid = false,
            validationError = DimensionValidator.Error.Empty,
        )
        val actual = validator("")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is unparsable to int, expected not valid with Unexpected error`() {
        val expected = ValidationResult<DimensionValidator.Error>(
            isValid = false,
            validationError = DimensionValidator.Error.Unexpected,
        )
        val actual = validator("5598❤")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is less than minimum allowed value, expected not valid with LessThanMinimum error`() {
        val expected = ValidationResult<DimensionValidator.Error>(
            isValid = false,
            validationError = DimensionValidator.Error.LessThanMinimum(MIN),
        )
        val actual = validator("55")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is bigger than maximum allowed value, expected not valid with BiggerThanMaximum error`() {
        val expected = ValidationResult<DimensionValidator.Error>(
            isValid = false,
            validationError = DimensionValidator.Error.BiggerThanMaximum(MAX),
        )
        val actual = validator("5598")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is valid parsable int value, expected valid`() {
        val expected = ValidationResult<DimensionValidator.Error>(true)
        val actual = validator("1024")
        Assert.assertEquals(expected, actual)
    }

    companion object {
        private const val MIN = 64
        private const val MAX = 2048
    }
}
