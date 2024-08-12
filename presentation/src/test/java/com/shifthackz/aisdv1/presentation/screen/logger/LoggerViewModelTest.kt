package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoggerViewModelTest : CoreViewModelTest<LoggerViewModel>() {

    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()
    private val stubMainRouter = mockk<MainRouter>()

    override fun initializeViewModel() = LoggerViewModel(
        fileProviderDescriptor = stubFileProviderDescriptor,
        mainRouter = stubMainRouter,
    )

    @Before
    override fun initialize() {
        super.initialize()
        every {
            stubFileProviderDescriptor.logsCacheDirPath
        } returns "/tmp/local"
    }

    @Test
    fun `initialize, read logs, expected loaded state`() {
        runTest {
            val expected = LoggerState(
                loading = false,
                text = ""
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

        viewModel.processIntent(LoggerIntent.NavigateBack)

        verify {
            stubMainRouter.navigateBack()
        }
    }
}
