package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter
import com.shifthackz.aisdv1.presentation.stub.stubDispatchersProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WebUiViewModelTest {

    private val stubRouter = mockk<WebUiRouter>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    @Before
    fun initialize() {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        every {
            stubPreferenceManager.automatic1111ServerUrl
        } returns "https://5598.is.my.favourite.com"
    }

    @Test
    fun `given initialized, expected UI state changed with correct stub values from preference`() =
        runTest {
            val expected = WebUiState(
                loading = false,
                source = ServerSource.AUTOMATIC1111,
                url = "https://5598.is.my.favourite.com",
            )
            val actual = createViewModel().state.value
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `given initialized with Swarm UI source, expected UI state uses swarm URL`() =
        runTest {
            every {
                stubPreferenceManager.source
            } returns ServerSource.SWARM_UI
            every {
                stubPreferenceManager.swarmUiServerUrl
            } returns "https://swarm.example.com"

            val expected = WebUiState(
                loading = false,
                source = ServerSource.SWARM_UI,
                url = "https://swarm.example.com",
            )
            val actual = createViewModel().state.value
            Assert.assertEquals(expected, actual)
        }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack called`() =
        runTest {
            every {
                stubRouter.navigateBack()
            } returns Unit

            createViewModel().processIntent(WebUiIntent.NavigateBack)

            verify {
                stubRouter.navigateBack()
            }
        }

    private fun TestScope.createViewModel() = WebUiViewModel(
        dispatchersProvider = stubDispatchersProvider,
        preferenceManager = stubPreferenceManager,
        router = stubRouter,
    )
}
