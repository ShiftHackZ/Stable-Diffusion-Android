package com.shifthackz.aisdv1.presentation.core

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import org.junit.Before
import org.robolectric.shadows.ShadowLog

interface CoreComposeTest {

    val composeTestRule: ComposeContentTestRule

    @Before
    @Throws(Exception::class)
    fun setUp() {
        // Redirect Logcat to console output to read printToLog Compose debug messages
        ShadowLog.stream = System.out
    }

    fun printComposeUiTreeToLog(tag: String, testTag: String? = null) {
        if (testTag.isNullOrEmpty()) {
            composeTestRule.onRoot().printToLog(tag)
        } else {
            composeTestRule.onNodeWithTag(testTag).printToLog(tag)
        }
    }

    fun onNodeWithTestTag(tag: String, parentTestTag: String? = null): SemanticsNodeInteraction =
        if (parentTestTag != null) {
            composeTestRule.onAllNodesWithTag(tag)
                .filterToOne(hasParent(hasTestTag(parentTestTag)))
                .assertIsDisplayed()
        } else {
            composeTestRule.onNodeWithTag(tag)
                .assertIsDisplayed()
        }

    fun retrieveTextFromNodeWithTestTag(tag: String, parentTestTag: String? = null): String =
        (onNodeWithTestTag(tag, parentTestTag)
            .fetchSemanticsNode().config
            .first { it.key.name == "Text" }
            .value as List<*>).first().toString()
}
