package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.presentation.model.ExtraType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore("ToDo: Investigate why sometimes tests fail on remote worker due to race-conditions.")
class EditTagViewModelTest {

    @Test
    fun `given received InitialData intent, expected UI state updated witch correct stub values`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

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

    @Test
    fun `given received UpdateTag intent, expected field currentTag changed, field originalTag not changed in UI state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EditTagIntent.UpdateTag("<lora:tag_5598:1>"))
        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("<lora:tag_5598:1>", state.currentTag)
        Assert.assertEquals("<lora:tag:1>", state.originalTag)
        Assert.assertNotEquals(state.originalTag, state.currentTag)
    }

    @Test
    fun `given received Value Increment intent, expected field currentTag changed, field originalTag not changed in UI state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EditTagIntent.Value.Increment)
        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("<lora:tag:1.25>", state.currentTag)
        Assert.assertEquals("<lora:tag:1>", state.originalTag)
        Assert.assertNotEquals(state.originalTag, state.currentTag)
    }

    @Test
    fun `given received Value Decrement intent, expected field currentTag changed, field originalTag not changed in UI state`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EditTagIntent.Value.Decrement)
        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("<lora:tag:0.75>", state.currentTag)
        Assert.assertEquals("<lora:tag:1>", state.originalTag)
        Assert.assertNotEquals(state.originalTag, state.currentTag)
    }

    @Test
    fun `given received Action Apply intent, expected ApplyPrompts effect with valid prompt delivered to effect collector`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EditTagIntent.Value.Increment)
        viewModel.processIntent(EditTagIntent.Action.Apply)
        advanceUntilIdle()

        val expected = "prompt <lora:tag:1.25>"
        val actual = (viewModel.effect.firstOrNull() as? EditTagEffect.ApplyPrompts)?.prompt
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given received Action Delete intent, expected ApplyPrompts effect with prompt that does not contain tag delivered to effect collector`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processIntent(EditTagIntent.Action.Delete)
        advanceUntilIdle()

        val expected = "prompt"
        val actual = (viewModel.effect.firstOrNull() as? EditTagEffect.ApplyPrompts)?.prompt
        Assert.assertEquals(expected, actual)
    }

    private fun TestScope.createViewModel(): EditTagViewModel {
        val dispatcher = StandardTestDispatcher(testScheduler)
        return EditTagViewModel(
            dispatchersProvider = testDispatchersProvider(dispatcher),
            prompt = "prompt <lora:tag:1>",
            negativePrompt = "negative",
            tag = "<lora:tag:1>",
            isNegative = false,
        )
    }

    private fun testDispatchersProvider(dispatcher: CoroutineDispatcher) = object : DispatchersProvider {
        override val io: CoroutineDispatcher = dispatcher
        override val ui: CoroutineDispatcher = dispatcher
        override val immediate: CoroutineDispatcher = dispatcher
    }
}
