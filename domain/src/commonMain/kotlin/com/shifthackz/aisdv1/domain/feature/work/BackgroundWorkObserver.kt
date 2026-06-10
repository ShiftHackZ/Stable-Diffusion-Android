package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface BackgroundWorkObserver {
    fun observeStatus(): Flow<BackgroundWorkStatus>
    fun observeResult(): Flow<BackgroundWorkResult>
    fun dismissResult()
    fun refreshStatus()
    fun postStatusMessage(title: String, subTitle: String)
    fun postSuccessSignal(result: List<AiGenerationResult>)
    fun postCancelSignal()
    fun postFailedSignal(t: Throwable)
    fun hasActiveTasks(): Boolean
}

object NoOpBackgroundWorkObserver : BackgroundWorkObserver {
    override fun observeStatus(): Flow<BackgroundWorkStatus> = flowOf(BackgroundWorkStatus(false, "", ""))
    override fun observeResult(): Flow<BackgroundWorkResult> = flowOf(BackgroundWorkResult.None)
    override fun dismissResult() = Unit
    override fun refreshStatus() = Unit
    override fun postStatusMessage(title: String, subTitle: String) = Unit
    override fun postSuccessSignal(result: List<AiGenerationResult>) = Unit
    override fun postCancelSignal() = Unit
    override fun postFailedSignal(t: Throwable) = Unit
    override fun hasActiveTasks(): Boolean = false
}
