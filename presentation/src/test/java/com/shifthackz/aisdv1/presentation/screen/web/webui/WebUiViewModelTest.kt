package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WebUiViewModelTest : CoreViewModelTest<WebUiViewModel>() {

    private val stubMainRouter = mockk<MainRouter>()
    private val stubPreferenceManager = mockk<PreferenceManager>()

    override fun initializeViewModel() = WebUiViewModel(
        mainRouter = stubMainRouter,
        preferenceManager = stubPreferenceManager,
    )

    @Before
    override fun initialize() {
        super.initialize()

        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        every {
            stubPreferenceManager.automatic1111ServerUrl
        } returns "https://5598.is.my.favourite.com"
    }

    @Test
    fun `given initialized, expected UI state changed with correct stub values from preference`() {
        runTest {
            val expected = WebUiState(
                false,
                ServerSource.AUTOMATIC1111,
                "https://5598.is.my.favourite.com"
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack() called`() {
        every {
            stubMainRouter.navigateBack()
        } returns Unit

        viewModel.processIntent(WebUiIntent.NavigateBack)

        verify {
            stubMainRouter.navigateBack()
        }
    }
}
