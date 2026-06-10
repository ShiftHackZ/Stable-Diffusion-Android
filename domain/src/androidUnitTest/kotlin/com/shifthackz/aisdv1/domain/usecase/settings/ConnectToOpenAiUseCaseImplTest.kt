package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestOpenAiApiKeyUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultConnectToOpenAiUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()
    private val stubTestOpenAiApiKeyUseCase = mockk<TestOpenAiApiKeyUseCase>()

    private val useCase = DefaultConnectToOpenAiUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
        testOpenAiApiKeyUseCase = stubTestOpenAiApiKeyUseCase,
    )

    @Test
    fun `given connection process successful, API key is valid, expected success result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } returns Unit

        coEvery {
            stubTestOpenAiApiKeyUseCase()
        } returns true

        assertEquals(Result.success(Unit), useCase("5598"))
    }

    @Test
    fun `given connection process successful, API key is NOT valid, expected success result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } returns Unit

        coEvery {
            stubTestOpenAiApiKeyUseCase()
        } returns false

        val actual = useCase("5598")

        assertTrue(actual.isFailure)
        assertTrue(actual.exceptionOrNull() is IllegalStateException)
        assertEquals("Bad key", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given connection process failed, expected error result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } throws stubThrowable

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } throws stubThrowable

        coEvery {
            stubTestOpenAiApiKeyUseCase()
        } throws stubThrowable

        val actual = useCase("5598")

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }
}
