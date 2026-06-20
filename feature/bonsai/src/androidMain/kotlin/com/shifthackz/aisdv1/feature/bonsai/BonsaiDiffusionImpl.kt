package com.shifthackz.aisdv1.feature.bonsai

import android.os.Process
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiDiffusion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Android Bonsai runtime entry point backed by the NDK bridge.
 *
 * The implementation keeps native execution off the caller thread, serializes
 * generation requests, and relays step progress through the shared local
 * diffusion status contract.
 */
internal class BonsaiDiffusionImpl : BonsaiDiffusion {

    private val mutex = Mutex()
    private val statusFlow = MutableSharedFlow<LocalDiffusionStatus>(replay = 1)

    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String = mutex.withLock {
        withContext(Dispatchers.Default) {
            AndroidBonsaiRequestValidator.validate(
                payload = payload,
                modelPath = modelPath,
            )
            BonsaiNativeBridge.ensureLoaded()
            val layout = AndroidBonsaiModelLayout.resolve(modelPath)
            statusFlow.tryEmit(LocalDiffusionStatus(current = 0, total = payload.samplingSteps))
            val job = currentCoroutineContext().job
            val cancellationHandle = job.invokeOnCompletion { cause ->
                if (cause is CancellationException) {
                    BonsaiNativeBridge.interrupt()
                }
            }

            try {
                withBackgroundThreadPriority {
                    BonsaiNativeBridge.generate(
                        layout = layout,
                        prompt = payload.prompt,
                        negativePrompt = payload.negativePrompt,
                        samplingSteps = payload.samplingSteps,
                        cfgScale = payload.cfgScale,
                        width = payload.width,
                        height = payload.height,
                        seed = payload.seed,
                        batchCount = payload.batchCount.coerceAtLeast(1),
                        allowNsfw = payload.nsfw,
                        backend = payload.bonsaiBackend.key,
                        callback = object : BonsaiNativeBridge.ProgressCallback {
                            override fun onProgress(current: Int, total: Int) {
                                statusFlow.tryEmit(
                                    LocalDiffusionStatus(
                                        current = current,
                                        total = total,
                                    ),
                                )
                            }
                        },
                    )
                }
            } finally {
                cancellationHandle.dispose()
            }
        }
    }

    private inline fun <T> withBackgroundThreadPriority(block: () -> T): T {
        val threadId = Process.myTid()
        val previousPriority = runCatching {
            Process.getThreadPriority(threadId)
        }.getOrNull()
        runCatching {
            Process.setThreadPriority(threadId, Process.THREAD_PRIORITY_BACKGROUND)
        }
        return try {
            block()
        } finally {
            if (previousPriority != null) {
                runCatching {
                    Process.setThreadPriority(threadId, previousPriority)
                }
            }
        }
    }

    override suspend fun interrupt() {
        if (BonsaiNativeBridge.isAvailable) {
            runCatching { BonsaiNativeBridge.interrupt() }
        }
    }

    override fun observeStatus(): Flow<LocalDiffusionStatus> = statusFlow
        .onStart { emit(LocalDiffusionStatus(current = 0, total = 0)) }
}
