package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestFalAiApiKeyUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultConnectToFalAiUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()
    private val stubTestFalAiApiKeyUseCase = mockk<TestFalAiApiKeyUseCase>()

    private val useCase = DefaultConnectToFalAiUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
        testFalAiApiKeyUseCase = stubTestFalAiApiKeyUseCase,
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
            stubTestFalAiApiKeyUseCase()
        } returns true

        assertEquals(Result.success(Unit), useCase("fal-key"))
        coVerify {
            stubSetServerConfigurationUseCase(
                match {
                    it.source == ServerSource.FAL_AI &&
                        it.falAiApiKey == "fal-key" &&
                        it.authCredentials == AuthorizationCredentials.None
                },
            )
        }
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
            stubTestFalAiApiKeyUseCase()
        } returns false

        val actual = useCase("fal-key")

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
            stubTestFalAiApiKeyUseCase()
        } throws stubThrowable

        val actual = useCase("fal-key")

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }
}
