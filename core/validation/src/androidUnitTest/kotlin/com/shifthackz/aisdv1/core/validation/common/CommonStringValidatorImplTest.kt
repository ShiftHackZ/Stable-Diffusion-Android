package com.shifthackz.aisdv1.core.validation.common

import com.shifthackz.aisdv1.core.validation.ValidationResult
import org.junit.Assert
import org.junit.Test

class CommonStringValidatorImplTest {

    private val validator = CommonStringValidatorImpl()

    @Test
    fun `given input is null, expected not valid with Empty error`() {
        val expected = ValidationResult<CommonStringValidator.Error>(
            isValid = false,
            validationError = CommonStringValidator.Error.Empty,
        )
        val actual = validator(null)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is empty, expected not valid with Empty error`() {
        val expected = ValidationResult<CommonStringValidator.Error>(
            isValid = false,
            validationError = CommonStringValidator.Error.Empty,
        )
        val actual = validator("")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is blank, expected not valid with Empty error`() {
        val expected = ValidationResult<CommonStringValidator.Error>(
            isValid = false,
            validationError = CommonStringValidator.Error.Empty,
        )
        val actual = validator(" ")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is non empty string, expected valid`() {
        val expected = ValidationResult<CommonStringValidator.Error>(true)
        val actual = validator("5598 is my favorite")
        Assert.assertEquals(expected, actual)
    }
}
