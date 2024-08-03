package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.aisdv1.presentation.core.CoreViewModelTest
import com.shifthackz.aisdv1.presentation.model.ExtraType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class EditTagViewModelTest : CoreViewModelTest<EditTagViewModel>() {

    override fun initializeViewModel() = EditTagViewModel()

    @Test
    fun `given received InitialData intent, expected UI state updated witch correct stub values`() {
        mockInitialData()
        runTest {
            val expected = EditTagState(
                prompt = "prompt <lora:tag:1>",
                negativePrompt = "negative",
                originalTag = "<lora:tag:1>",
                currentTag = "<lora:tag:1>",
                extraType = ExtraType.Lora,
                isNegative = false,
            )
            val actual = viewModel.state.value
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received UpdateTag intent, expected field currentTag changed, field originalTag not changed in UI state`() {
        mockInitialData()
        viewModel.processIntent(EditTagIntent.UpdateTag("<lora:tag_5598:1>"))
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("<lora:tag_5598:1>", state.currentTag)
            Assert.assertEquals("<lora:tag:1>", state.originalTag)
            Assert.assertNotEquals(state.originalTag, state.currentTag)
        }
    }

    @Test
    fun `given received Value Increment intent, expected field currentTag changed, field originalTag not changed in UI state`() {
        mockInitialData()
        viewModel.processIntent(EditTagIntent.Value.Increment)
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("<lora:tag:1.25>", state.currentTag)
            Assert.assertEquals("<lora:tag:1>", state.originalTag)
            Assert.assertNotEquals(state.originalTag, state.currentTag)
        }
    }

    @Test
    fun `given received Value Decrement intent, expected field currentTag changed, field originalTag not changed in UI state`() {
        mockInitialData()
        viewModel.processIntent(EditTagIntent.Value.Decrement)
        runTest {
            val state = viewModel.state.value
            Assert.assertEquals("<lora:tag:0.75>", state.currentTag)
            Assert.assertEquals("<lora:tag:1>", state.originalTag)
            Assert.assertNotEquals(state.originalTag, state.currentTag)
        }
    }

    @Test
    fun `given received Action Apply intent, expected ApplyPrompts effect with valid prompt delivered to effect collector`() {
        mockInitialData()
        viewModel.processIntent(EditTagIntent.Value.Increment)
        viewModel.processIntent(EditTagIntent.Action.Apply)
        runTest {
            val expected = "prompt <lora:tag:1.25>"
            val actual = (viewModel.effect.firstOrNull() as? EditTagEffect.ApplyPrompts)?.prompt
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    fun `given received Action Delete intent, expected ApplyPrompts effect with prompt that does not contain tag delivered to effect collector`() {
        mockInitialData()
        viewModel.processIntent(EditTagIntent.Action.Delete)
        runTest {
            val expected = "prompt"
            val actual = (viewModel.effect.firstOrNull() as? EditTagEffect.ApplyPrompts)?.prompt
            Assert.assertEquals(expected, actual)
        }
    }

    private fun mockInitialData() {
        val intent = EditTagIntent.InitialData(
            prompt = "prompt <lora:tag:1>",
            negativePrompt = "negative",
            tag = "<lora:tag:1>",
            isNegative = false,
        )
        viewModel.processIntent(intent)
    }
}
