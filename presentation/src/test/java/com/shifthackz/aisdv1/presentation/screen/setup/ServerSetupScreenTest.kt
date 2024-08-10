package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.presentation.core.CoreComposeTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class ServerSetupScreenTest : CoreComposeTest {

    @get:Rule
    override val composeTestRule = createComposeRule()

    private val stubUiState = MutableStateFlow(ServerSetupState())

    private val stubViewModel = mockk<ServerSetupViewModel>()

    @Before
    fun initialize() {

        every {
            stubViewModel.state
        } returns stubUiState

        every {
            stubViewModel.effect
        } returns flowOf()

        every {
            stubViewModel.processIntent(any())
        } returns Unit

        startKoin {
            modules(
                module {
                    factory<BuildInfoProvider> {
                        object : BuildInfoProvider {
                            override val isDebug: Boolean
                                get() = false
                            override val buildNumber: Int
                                get() = 0
                            override val version: BuildVersion
                                get() = BuildVersion()
                            override val type: BuildType
                                get() = BuildType.FOSS
                        }
                    }
                }
            )
        }
    }

    @After
    fun finalize() {
        stopKoin()
    }

    @Test
    fun `server setup action button displayed and enabled before and after click`() {
        composeTestRule.setContent {
            ServerSetupScreenContent(state = ServerSetupState())
        }
        val setupButton = onNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)
        val setupButtonInitialText = retrieveTextFromNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)
        println("TEST | setupButtonInitialText=$setupButtonInitialText")
        printComposeUiTreeToLog(TAG, ServerSetupScreenTags.MAIN_BUTTON)
        setupButton.assertIsDisplayed().assertIsEnabled()

        setupButton.performClick()
        val setupButtonNewText = retrieveTextFromNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)
        println("TEST | setupButtonNewText=$setupButtonNewText")
        printComposeUiTreeToLog(TAG, ServerSetupScreenTags.MAIN_BUTTON)
        setupButton.assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun `given user is on SOURCE tab, clicks Next, expected button text changed to Connect`() {
        composeTestRule.setContent {
            ServerSetupScreen(viewModel = stubViewModel)
        }
        val setupButton = onNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)

        setupButton.performClick()
        stubUiState.update {
            it.copy(
                step = ServerSetupState.Step.CONFIGURE
            )
        }

        setupButton
            .assertIsDisplayed()
            .assertTextEquals("Connect")
    }

    companion object {
        const val TAG = "ServerSetupScreenTest"
    }
}
