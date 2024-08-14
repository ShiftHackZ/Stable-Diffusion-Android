package com.shifthackz.aisdv1.core.validation.path

import com.shifthackz.aisdv1.core.validation.ValidationResult
import org.junit.Assert
import org.junit.Test

class FilePathValidatorImplTest {

    private val validator = FilePathValidatorImpl()

    @Test
    fun `iven input is null, expected not valid with Empty error`() {
        val expected = ValidationResult<FilePathValidator.Error>(
            isValid = false,
            validationError = FilePathValidator.Error.Empty,
        )
        val actual = validator(null)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is empty, expected not valid with Empty error`() {
        val expected = ValidationResult<FilePathValidator.Error>(
            isValid = false,
            validationError = FilePathValidator.Error.Empty,
        )
        val actual = validator("")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is blank, expected not valid with Empty error`() {
        val expected = ValidationResult<FilePathValidator.Error>(
            isValid = false,
            validationError = FilePathValidator.Error.Empty,
        )
        val actual = validator(" ")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is not valid, expected not valid with Invalid error`() {
        val expected = ValidationResult<FilePathValidator.Error>(
            isValid = false,
            validationError = FilePathValidator.Error.Invalid,
        )
        val actual = validator("cc")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is valid, expected valid`() {
        val expected = ValidationResult<FilePathValidator.Error>(true)
        val actual = validator("/tmp/local/5598")
        Assert.assertEquals(expected, actual)
    }
}
