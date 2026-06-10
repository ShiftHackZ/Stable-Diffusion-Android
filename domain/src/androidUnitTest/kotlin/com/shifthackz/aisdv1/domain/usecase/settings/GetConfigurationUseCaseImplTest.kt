package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultGetConfigurationUseCaseImplTest {

    private val stubConfigurationStore = ConfigurationStoreStub(mockConfiguration)
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val useCase = DefaultGetConfigurationUseCaseImpl(
        configurationStore = stubConfigurationStore,
        authorizationStore = stubAuthorizationStore,
    )

    @Test
    fun `given configuration read success, expected valid configuration domain model value`() = runTest {
        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        assertEquals(mockConfiguration, useCase())
    }
}
