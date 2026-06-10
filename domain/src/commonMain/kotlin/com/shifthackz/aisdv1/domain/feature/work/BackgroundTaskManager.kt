package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `BackgroundTaskManager` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface BackgroundTaskManager {
    /**
     * Executes the `scheduleTextToImageTask` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    fun scheduleTextToImageTask(payload: TextToImagePayload)
    /**
     * Executes the `scheduleImageToImageTask` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    fun scheduleImageToImageTask(payload: ImageToImagePayload)
    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI domain layer.
     *
     * @return Result produced by `retryLastTextToImageTask`.
     * @author Dmitriy Moroz
     */
    fun retryLastTextToImageTask(): Result<Unit>
    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI domain layer.
     *
     * @return Result produced by `retryLastImageToImageTask`.
     * @author Dmitriy Moroz
     */
    fun retryLastImageToImageTask(): Result<Unit>
    /**
     * Executes the `cancelAll` step in the SDAI domain layer.
     *
     * @return Result produced by `cancelAll`.
     * @author Dmitriy Moroz
     */
    fun cancelAll(): Result<Unit>
}

/**
 * Provides the `NoOpBackgroundTaskManager` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpBackgroundTaskManager : BackgroundTaskManager {
    /**
     * Executes the `scheduleTextToImageTask` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override fun scheduleTextToImageTask(payload: TextToImagePayload) = Unit
    /**
     * Executes the `scheduleImageToImageTask` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override fun scheduleImageToImageTask(payload: ImageToImagePayload) = Unit
    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastTextToImageTask(): Result<Unit> = Result.failure(UnsupportedOperationException())
    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastImageToImageTask(): Result<Unit> = Result.failure(UnsupportedOperationException())
    /**
     * Executes the `cancelAll` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun cancelAll(): Result<Unit> = Result.success(Unit)
}
