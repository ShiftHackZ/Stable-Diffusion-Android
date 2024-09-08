@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.core.CoreComposeTest
import com.shifthackz.aisdv1.presentation.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.withNewState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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
        Dispatchers.setMain(UnconfinedTestDispatcher())
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
        Dispatchers.resetMain()
    }

    @Test
    fun `given user is on SOURCE tab with LOCAL server source, clicks Next, expected button is disabled, text changed to Setup`() {
        composeTestRule.setContent {
            ServerSetupScreen(viewModel = stubViewModel)
        }
        val setupButton = onNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)
        setupButton
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertTextEquals("Next")
        printComposeUiTreeToLog(TAG, ServerSetupScreenTags.MAIN_BUTTON)
        setupButton.performClick()
        stubUiState.update {
            it.copy(
                step = ServerSetupState.Step.CONFIGURE,
                mode = ServerSource.LOCAL_MICROSOFT_ONNX
            )
        }

        setupButton
            .assertIsDisplayed()
            .assertIsNotEnabled()
            .assertTextEquals("Setup")
    }

    @Test
    fun `given user is on CONFIGURE tab with LOCAL server source, clicks Switch, expected main button with Setup text becomes enabled then clicks Switch again, expected main button with Setup text becomes disabled`() {
        composeTestRule.setContent {
            ServerSetupScreen(viewModel = stubViewModel)
        }
        stubUiState.update {
            it.copy(
                step = ServerSetupState.Step.CONFIGURE,
                mode = ServerSource.LOCAL_MICROSOFT_ONNX,
                localOnnxModels = mockLocalAiModels.mapToUi()
            )
        }
        val setupButton = onNodeWithTestTag(ServerSetupScreenTags.MAIN_BUTTON)
        val switch = onNodeWithTestTag(ServerSetupScreenTags.CUSTOM_MODEL_SWITCH)
        setupButton
            .assertIsDisplayed()
            .assertIsNotEnabled()
            .assertTextEquals("Setup")
        switch
            .assertIsDisplayed()
            .assertIsOff()
            .assertIsEnabled()

        switch.performClick()
        stubUiState.update {
            it.copy(
                localOnnxCustomModel = true,
                localOnnxModels = it.localOnnxModels.withNewState(
                    it.localOnnxModels.find { m -> m.id == LocalAiModel.CustomOnnx.id }!!.copy(
                        selected = true,
                        downloaded = true
                    ),
                ),
            )
        }

        setupButton
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertTextEquals("Setup")
        switch
            .assertIsDisplayed()
            .assertIsOn()
            .assertIsEnabled()

        switch.performClick()
        stubUiState.update {
            it.copy(
                localOnnxCustomModel = false,
                localOnnxModels = it.localOnnxModels.withNewState(
                    it.localOnnxModels.find { m -> m.id == LocalAiModel.CustomOnnx.id }!!.copy(
                        selected = false,
                    ),
                ),
            )
        }

        setupButton
            .assertIsDisplayed()
            .assertIsNotEnabled()
            .assertTextEquals("Setup")
        switch
            .assertIsDisplayed()
            .assertIsOff()
            .assertIsEnabled()
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
