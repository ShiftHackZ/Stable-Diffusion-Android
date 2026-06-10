package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface BackgroundTaskManager {
    fun scheduleTextToImageTask(payload: TextToImagePayload)
    fun scheduleImageToImageTask(payload: ImageToImagePayload)
    fun retryLastTextToImageTask(): Result<Unit>
    fun retryLastImageToImageTask(): Result<Unit>
    fun cancelAll(): Result<Unit>
}

object NoOpBackgroundTaskManager : BackgroundTaskManager {
    override fun scheduleTextToImageTask(payload: TextToImagePayload) = Unit
    override fun scheduleImageToImageTask(payload: ImageToImagePayload) = Unit
    override fun retryLastTextToImageTask(): Result<Unit> = Result.failure(UnsupportedOperationException())
    override fun retryLastImageToImageTask(): Result<Unit> = Result.failure(UnsupportedOperationException())
    override fun cancelAll(): Result<Unit> = Result.success(Unit)
}
