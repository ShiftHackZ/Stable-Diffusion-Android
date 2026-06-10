package com.shifthackz.aisdv1.work

import androidx.work.WorkInfo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.work.di.WorkManagerProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.koin.java.KoinJavaComponent.inject

internal class BackgroundWorkObserverImpl : BackgroundWorkObserver {

    private val stateFlow = MutableStateFlow(false)
    private val messageFlow = MutableStateFlow("" to "")
    private val resultFlow = MutableStateFlow<BackgroundWorkResult>(BackgroundWorkResult.None)

    override fun observeStatus(): Flow<BackgroundWorkStatus> {
        refreshStatus()
        return combine(stateFlow, messageFlow) { running, (title, subTitle) ->
            BackgroundWorkStatus(running, title, subTitle)
        }
    }

    override fun observeResult(): Flow<BackgroundWorkResult> = resultFlow

    override fun dismissResult() {
        resultFlow.value = BackgroundWorkResult.None
    }

    override fun refreshStatus() {
        stateFlow.value = hasActiveTasks()
    }

    override fun postStatusMessage(title: String, subTitle: String) {
        stateFlow.value = true
        messageFlow.value = title to subTitle
        resultFlow.value = BackgroundWorkResult.None
    }

    override fun postSuccessSignal(result: List<AiGenerationResult>) {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.Success(result)
    }

    override fun postCancelSignal() {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.None
    }

    override fun postFailedSignal(t: Throwable) {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.Error(t)
    }

    override fun hasActiveTasks(): Boolean {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workInfos = workManager().getWorkInfosByTag(Constants.TAG_GENERATION).get()
        val isRunning = workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.BLOCKED
                    || workInfo.state == WorkInfo.State.ENQUEUED
                    || workInfo.state == WorkInfo.State.RUNNING
        }
        return isRunning
    }
}
