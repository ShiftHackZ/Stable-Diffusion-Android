package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultSetServerConfigurationUseCaseImplTest {

    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val useCase = DefaultSetServerConfigurationUseCaseImpl(
        configurationStore = stubConfigurationStore,
        authorizationStore = stubAuthorizationStore,
    )

    @Test
    fun `given configuration apply success, expected complete value`() = runTest {
        every {
            stubAuthorizationStore.storeAuthorizationCredentials(any())
        } returns Unit

        useCase(mockConfiguration)

        assertEquals(mockConfiguration, stubConfigurationStore.getConfiguration(mockConfiguration.authCredentials))
    }
}
