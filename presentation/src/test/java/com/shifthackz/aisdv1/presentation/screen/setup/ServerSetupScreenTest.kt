package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shifthackz.aisdv1.presentation.utils.ComposeTestUtils
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class ServerSetupScreenTest : ComposeTestUtils {

    @get:Rule
    override val composeTestRule = createComposeRule()

    @Test
    fun `server setup action button displayed and enabled before and after click`() {
        composeTestRule.setContent {
            ServerSetupScreenContent(state = ServerSetupState())
        }
        val setupButton = onNodeWithTestTag(SETUP_BUTTON_TAG)
        val setupButtonInitialText = retrieveTextFromNodeWithTestTag(SETUP_BUTTON_TAG)
        println("TEST | setupButtonInitialText=$setupButtonInitialText")
        printComposeUiTreeToLog(TAG, SETUP_BUTTON_TAG)
        setupButton.assertIsDisplayed().assertIsEnabled()

        setupButton.performClick()
        val setupButtonNewText = retrieveTextFromNodeWithTestTag(SETUP_BUTTON_TAG)
        println("TEST | setupButtonNewText=$setupButtonNewText")
        printComposeUiTreeToLog(TAG, SETUP_BUTTON_TAG)
        setupButton.assertIsDisplayed().assertIsEnabled()
    }

    companion object {
        const val TAG = "ServerSetupScreenTest"
        const val SETUP_BUTTON_TAG = "ServerSetupActionButton"
    }
}
