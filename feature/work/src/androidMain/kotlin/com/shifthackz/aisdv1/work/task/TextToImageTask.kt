package com.shifthackz.aisdv1.work.task

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.work.Constants
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_TEXT_TO_IMAGE_GENERIC
import com.shifthackz.aisdv1.work.core.CoreGenerationWorker
import com.shifthackz.aisdv1.work.mappers.toTextToImagePayload
import kotlinx.coroutines.CancellationException
import java.io.File

/**
 * Coordinates `TextToImageTask` behavior in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class TextToImageTask(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    activityIntentProvider: ActivityIntentProvider,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    interruptGenerationUseCase: InterruptGenerationUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `textToImageUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val textToImageUseCase: TextToImageUseCase,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : CoreGenerationWorker(
    context = context,
    workerParameters = workerParameters,
    pushNotificationManager = pushNotificationManager,
    activityIntentProvider = activityIntentProvider,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
    observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
    interruptGenerationUseCase = interruptGenerationUseCase,
) {

    override val notificationId: Int = NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND

    override val genericNotificationId: Int = NOTIFICATION_TEXT_TO_IMAGE_GENERIC

    override suspend fun doWork(): Result {
        // Workaround for LocalDiffusion provider:
        //
        // If LocalDiffusion process previously died, prevent WorkManager to go to infinite
        // task repeat loop.
        if (preferenceManager.backgroundProcessCount > 0) {
            handleProcess()
            handleError(Throwable("Background process count > 0"))
            clearSubscriptions()
            preferenceManager.backgroundProcessCount = 0
            debugLog("Background process count > 0! Skipping task.")
            return Result.failure()
        }

        preferenceManager.backgroundProcessCount++
        handleStart()
        backgroundWorkObserver.refreshStatus()
        backgroundWorkObserver.dismissResult()
        debugLog("Starting TextToImageTask!")

        return try {
            val file = File(fileProviderDescriptor.workCacheDirPath, Constants.FILE_TEXT_TO_IMAGE)
            if (!file.exists()) {
                preferenceManager.backgroundProcessCount--
                val t = Throwable("File is null.")
                handleError(t)
                clearSubscriptions()
                errorLog(t, "Payload file does not exist.")
                return Result.failure()
            }

            val bytes = file.readBytes()
            val payload = bytes.toTextToImagePayload()

            if (payload == null) {
                preferenceManager.backgroundProcessCount--
                val t = Throwable("Payload is null.")
                handleError(t)
                clearSubscriptions()
                errorLog(t, "Payload was failed to read/parse.")
                return Result.failure()
            }

            listenHordeStatus()
            listenLocalDiffusionStatus()

            handleProcess()
            runCatching { textToImageUseCase(payload) }
                .fold(
                    onSuccess = { result ->
                        preferenceManager.backgroundProcessCount--
                        handleSuccess(result)
                        debugLog("Generation finished successfully!")
                        Result.success()
                    },
                    onFailure = { t ->
                        if (t is CancellationException) throw t
                        preferenceManager.backgroundProcessCount--
                        handleError(t)
                        errorLog(t, "Caught exception from TextToImageUseCase!")
                        Result.failure()
                    },
                )
        } catch (e: CancellationException) {
            handleCancellation()
            throw e
        } catch (e: Exception) {
            preferenceManager.backgroundProcessCount--
            handleError(e)
            errorLog(e, "Caught exception from TextToImageTask worker!")
            Result.failure()
        } finally {
            clearSubscriptions()
        }
    }
}
