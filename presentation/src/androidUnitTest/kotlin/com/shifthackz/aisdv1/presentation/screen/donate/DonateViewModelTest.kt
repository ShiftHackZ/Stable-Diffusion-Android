package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.domain.usecase.donate.FetchSupportersUseCase
import com.shifthackz.aisdv1.presentation.mocks.mockSupporters
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DonateViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchersProvider = object : DispatchersProvider {
        override val io: CoroutineDispatcher = testDispatcher
        override val ui: CoroutineDispatcher = testDispatcher
        override val immediate: CoroutineDispatcher = testDispatcher
    }
    private val stubException = Throwable("Something went wrong.")
    private val stubFetchSupportersUseCase = mockk<FetchSupportersUseCase>()
    private val stubLinksProvider = mockk<LinksProvider>()
    private val stubDonateRouter = mockk<DonateRouter>()

    @Test
    fun `initialized, fetch successful, expected UI state with loading false and non empty supporters`() = runTest(testDispatcher) {
        coEvery {
            stubFetchSupportersUseCase()
        } returns mockSupporters

        val viewModel = createViewModel()
        advanceUntilIdle()

        val expected = DonateState(
            loading = false,
            supporters = mockSupporters,
        )
        val actual = viewModel.state.value
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `initialized, fetch failed, expected UI state with loading false and empty supporters`() = runTest(testDispatcher) {
        coEvery {
            stubFetchSupportersUseCase()
        } throws stubException

        val viewModel = createViewModel()
        advanceUntilIdle()

        val expected = DonateState(
            loading = false,
            supporters = emptyList(),
        )
        val actual = viewModel.state.value
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received NavigateBack intent, expected router navigateBack called`() = runTest(testDispatcher) {
        coEvery {
            stubFetchSupportersUseCase()
        } returns mockSupporters
        every {
            stubDonateRouter.navigateBack()
        } returns Unit

        val viewModel = createViewModel()
        viewModel.processIntent(DonateIntent.NavigateBack)

        verify {
            stubDonateRouter.navigateBack()
        }
    }

    private fun TestScope.createViewModel() = DonateViewModel(
        dispatchersProvider = dispatchersProvider,
        fetchSupportersUseCase = stubFetchSupportersUseCase,
        linksProvider = stubLinksProvider,
        router = stubDonateRouter,
    )
}
