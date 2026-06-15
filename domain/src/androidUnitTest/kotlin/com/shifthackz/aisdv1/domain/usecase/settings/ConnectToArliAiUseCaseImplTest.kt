package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestArliAiApiKeyUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultConnectToArliAiUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()
    private val stubTestArliAiApiKeyUseCase = mockk<TestArliAiApiKeyUseCase>()

    private val useCase = DefaultConnectToArliAiUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
        testArliAiApiKeyUseCase = stubTestArliAiApiKeyUseCase,
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
            stubTestArliAiApiKeyUseCase()
        } returns true

        assertEquals(Result.success(Unit), useCase("arli-key"))
        coVerify {
            stubSetServerConfigurationUseCase(
                match {
                    it.source == ServerSource.ARLI_AI &&
                        it.arliAiApiKey == "arli-key" &&
                        it.authCredentials == AuthorizationCredentials.None
                },
            )
        }
    }

    @Test
    fun `given connection process successful, API key is NOT valid, expected failure result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } returns Unit

        coEvery {
            stubTestArliAiApiKeyUseCase()
        } returns false

        val actual = useCase("arli-key")

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
            stubTestArliAiApiKeyUseCase()
        } throws stubThrowable

        val actual = useCase("arli-key")

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }

}
