package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectToLocalDiffusionUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()

    private val useCase = ConnectToLocalDiffusionUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
    )

    @Test
    fun `given connection process successful, expected success result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } returns Unit

        assertEquals(Result.success(Unit), useCase("5598", "/stub/model"))
    }

    @Test
    fun `given connection process failed, expected error result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } throws stubThrowable

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } throws stubThrowable

        val actual = useCase("5598", "/stub/model")

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }
}
