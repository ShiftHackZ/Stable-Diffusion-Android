package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectToBonsaiUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()

    private val useCase = ConnectToBonsaiUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
    )

    @Test
    fun `given connection process successful, expected bonsai configuration saved`() = runTest {
        val configurationSlot = slot<Configuration>()
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration
        coEvery {
            stubSetServerConfigurationUseCase(capture(configurationSlot))
        } returns Unit

        val actual = useCase("bonsai-1", "/stub/bonsai")

        assertEquals(Result.success(Unit), actual)
        assertEquals(ServerSource.LOCAL_APPLE_BONSAI, configurationSlot.captured.source)
        assertEquals("bonsai-1", configurationSlot.captured.localBonsaiModelId)
        assertEquals("/stub/bonsai", configurationSlot.captured.localBonsaiModelPath)
    }

    @Test
    fun `given connection process failed, expected error result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } throws stubThrowable
        coEvery {
            stubSetServerConfigurationUseCase(any())
        } throws stubThrowable

        val actual = useCase("bonsai-1", "/stub/bonsai")

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }
}
