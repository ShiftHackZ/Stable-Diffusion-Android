package com.shifthackz.aisdv1.work

import androidx.work.WorkInfo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.work.di.WorkManagerProvider
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.koin.java.KoinJavaComponent.inject

internal class BackgroundWorkObserverImpl : BackgroundWorkObserver {

    private val stateSubject = BehaviorSubject.createDefault(false)
    private val messageSubject = BehaviorSubject.createDefault("" to "")
    private val resultSubject = BehaviorSubject.createDefault<BackgroundWorkResult>(BackgroundWorkResult.None)

    override fun observeStatus(): Flowable<BackgroundWorkStatus> {
        refreshStatus()
        return Flowable.combineLatest(
            stateSubject.toFlowable(BackpressureStrategy.LATEST),
            messageSubject.toFlowable(BackpressureStrategy.LATEST),
        ) { running, statusMessage ->
            BackgroundWorkStatus(running, statusMessage.first, statusMessage.second)
        }
    }

    override fun observeResult(): Flowable<BackgroundWorkResult> {
        return resultSubject.toFlowable(BackpressureStrategy.LATEST)
    }

    override fun dismissResult() {
        resultSubject.onNext(BackgroundWorkResult.None)
    }

    override fun refreshStatus() {
        val hasActive = hasActiveTasks()
        if (hasActive) {
            resultSubject.onNext(BackgroundWorkResult.None)
        }
        stateSubject.onNext(hasActive)
    }

    override fun postStatusMessage(title: String, subTitle: String) {
        stateSubject.onNext(true)
        messageSubject.onNext(title to subTitle)
        resultSubject.onNext(BackgroundWorkResult.None)
    }

    override fun postSuccessSignal(result: List<AiGenerationResult>) {
        stateSubject.onNext(false)
        messageSubject.onNext("" to "")
        resultSubject.onNext(BackgroundWorkResult.Success(result))
    }

    override fun postCancelSignal() {
        stateSubject.onNext(false)
        messageSubject.onNext("" to "")
        resultSubject.onNext(BackgroundWorkResult.None)
    }

    override fun postFailedSignal(t: Throwable) {
        stateSubject.onNext(false)
        messageSubject.onNext("" to "")
        resultSubject.onNext(BackgroundWorkResult.Error(t))
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
