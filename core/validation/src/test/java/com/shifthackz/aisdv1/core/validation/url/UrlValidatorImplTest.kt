package com.shifthackz.aisdv1.core.validation.url

import android.webkit.URLUtil
import com.shifthackz.aisdv1.core.validation.ValidationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Test
import java.util.regex.Matcher
import java.util.regex.Pattern

class UrlValidatorImplTest {

    private val stubMatcher = mockk<Matcher>()
    private val stubPattern = mockk<Pattern>()

    private val validator = UrlValidatorImpl(stubPattern)

    @Test
    fun `given input is null, expected not valid with Empty error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Empty,
        )
        val actual = validator(null)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is empty, expected not valid with Empty error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Empty,
        )
        val actual = validator("")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is blank, expected not valid with Empty error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Empty,
        )
        val actual = validator(" ")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is url with ftp protocol, expected not valid with BadScheme error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.BadScheme,
        )
        val actual = validator("ftp://5598.is.my.favorite.com:21/i_failed.dat")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is url with port 99999, expected not valid with BadPort error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.BadPort,
        )
        val actual = validator("http://5598.is.my.favorite.com:99999")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is http localhost ipv4 address, expected not valid with Localhost error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Localhost,
        )
        val actual = validator("http://127.0.0.1:7860")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is https localhost ipv4 address, expected not valid with Localhost error`() {
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Localhost,
        )
        val actual = validator("https://127.0.0.1:7860")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is not valid url, expected not valid with Invalid error`() {
        val mockInput = "https://968.666.777.5598:00000000"
        mockkConstructor(URLUtil::class)
        mockkStatic(URLUtil::class)
        every {
            URLUtil.isValidUrl(mockInput)
        } returns false
        val expected = ValidationResult<UrlValidator.Error>(
            isValid = false,
            validationError = UrlValidator.Error.Invalid,
        )
        val actual = validator(mockInput)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given input is valid url, expected valid`() {
        val mockInput = "https://192.168.0.1:7860"
        mockkConstructor(URLUtil::class)
        mockkStatic(URLUtil::class)
        every {
            URLUtil.isValidUrl(mockInput)
        } returns true
        every {
            stubMatcher.matches()
        } returns true
        every {
            stubPattern.matcher(any())
        } returns stubMatcher

        val expected = ValidationResult<UrlValidator.Error>(true)
        val actual = validator(mockInput)
        Assert.assertEquals(expected, actual)
    }
}
