package com.shifthackz.aisdv1.work

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.localization.formatter.DurationFormatter
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveBonsaiProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveCoreMlProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Implements `BackgroundTaskManager` behavior for iOS background generation.
 *
 * @author Dmitriy Moroz
 */
internal class BackgroundTaskManagerImpl(
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val textToImageUseCase: TextToImageUseCase,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val observeCoreMlProcessStatusUseCase: ObserveCoreMlProcessStatusUseCase,
    private val observeBonsaiProcessStatusUseCase: ObserveBonsaiProcessStatusUseCase,
    private val preferenceManager: PreferenceManager,
) : BackgroundTaskManager {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val statusScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var activeJob: Job? = null
    private var lastTextToImagePayload: TextToImagePayload? = null
    private var lastImageToImagePayload: ImageToImagePayload? = null

    /**
     * Executes the `scheduleTextToImageTask` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun scheduleTextToImageTask(payload: TextToImagePayload) {
        lastTextToImagePayload = payload
        runGenerationTask {
            textToImageUseCase(payload)
        }
    }

    /**
     * Executes the `scheduleImageToImageTask` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun scheduleImageToImageTask(payload: ImageToImagePayload) {
        lastImageToImagePayload = payload
        runGenerationTask {
            imageToImageUseCase(payload)
        }
    }

    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastTextToImageTask(): Result<Unit> {
        val payload = lastTextToImagePayload
            ?: return Result.failure(Throwable("Payload is null."))
        scheduleTextToImageTask(payload)
        return Result.success(Unit)
    }

    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun retryLastImageToImageTask(): Result<Unit> {
        val payload = lastImageToImagePayload
            ?: return Result.failure(Throwable("Payload is null."))
        scheduleImageToImageTask(payload)
        return Result.success(Unit)
    }

    /**
     * Executes the `cancelAll` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun cancelAll(): Result<Unit> = runCatching {
        activeJob?.cancel()
        clearStatusSubscriptions()
        coroutineScope.launch {
            runCatching { interruptGenerationUseCase() }
        }
        backgroundWorkObserver.postCancelSignal()
    }

    private fun runGenerationTask(block: suspend () -> List<AiGenerationResult>) {
        activeJob?.cancel()
        clearStatusSubscriptions()
        backgroundWorkObserver.dismissResult()

        lateinit var generationJob: Job
        generationJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
            val backgroundExecution = IosBackgroundExecution(
                name = "SDAI Generation",
                activeJob = { generationJob },
            )
            try {
                backgroundExecution.begin()
                listenSourceStatus()
                postRunningMessage()
                val result = block()
                if (isActiveJob(generationJob)) {
                    backgroundWorkObserver.postSuccessSignal(result)
                }
            } catch (e: CancellationException) {
                if (isActiveJob(generationJob)) {
                    backgroundWorkObserver.postCancelSignal()
                }
            } catch (t: Throwable) {
                if (isActiveJob(generationJob)) {
                    backgroundWorkObserver.postFailedSignal(t)
                }
            } finally {
                if (isActiveJob(generationJob)) {
                    clearStatusSubscriptions()
                    activeJob = null
                }
                backgroundExecution.end()
            }
        }
        activeJob = generationJob
        generationJob.start()
    }

    private fun isActiveJob(job: Job): Boolean = activeJob === job

    private fun listenSourceStatus() {
        when (preferenceManager.source) {
            ServerSource.HORDE -> listenHordeStatus()
            ServerSource.LOCAL_MICROSOFT_ONNX -> listenLocalDiffusionStatus()
            ServerSource.LOCAL_APPLE_CORE_ML -> listenCoreMlStatus()
            ServerSource.LOCAL_APPLE_BONSAI -> listenBonsaiStatus()
            else -> Unit
        }
    }

    private fun listenHordeStatus() {
        statusScope.launch {
            observeHordeProcessStatusUseCase()
                .catch { /* Status updates are best-effort on iOS background work. */ }
                .collect { status ->
                    val title = Localization.string("notification_running_title")
                    val subTitle = buildString {
                        appendLine(
                            Localization.string(
                                "communicating_status_queue",
                                "${status.queuePosition}",
                            ),
                        )
                        append(
                            Localization.string(
                                "communicating_wait_time",
                                DurationFormatter.formatDurationInSeconds(status.waitTimeSeconds),
                            ),
                        )
                    }
                    backgroundWorkObserver.postStatusMessage(title, subTitle)
                }
        }
    }

    private fun listenLocalDiffusionStatus() {
        statusScope.launch {
            observeLocalDiffusionProcessStatusUseCase()
                .catch { /* Status updates are best-effort on iOS background work. */ }
                .collect { status ->
                    postStepMessage(status.current, status.total)
                }
        }
    }

    private fun listenCoreMlStatus() {
        statusScope.launch {
            observeCoreMlProcessStatusUseCase()
                .catch { /* Status updates are best-effort on iOS background work. */ }
                .collect { status ->
                    postStepMessage(status.current, status.total)
                }
        }
    }

    private fun listenBonsaiStatus() {
        statusScope.launch {
            observeBonsaiProcessStatusUseCase()
                .catch { /* Status updates are best-effort on iOS background work. */ }
                .collect { status ->
                    postStepMessage(status.current, status.total)
                }
        }
    }

    private fun postRunningMessage() {
        backgroundWorkObserver.postStatusMessage(
            title = Localization.string("notification_running_title"),
            subTitle = Localization.string("notification_running_sub_title"),
        )
    }

    private fun postStepMessage(current: Int, total: Int) {
        backgroundWorkObserver.postStatusMessage(
            title = Localization.string("notification_running_title"),
            subTitle = Localization.string(
                "communicating_status_steps",
                current.toString(),
                total.toString(),
            ),
        )
    }

    private fun clearStatusSubscriptions() {
        statusScope.coroutineContext.cancelChildren()
    }
}
