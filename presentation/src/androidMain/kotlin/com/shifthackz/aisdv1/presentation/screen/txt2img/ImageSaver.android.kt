package com.shifthackz.aisdv1.presentation.screen.txt2img

import android.content.Context
import android.util.Base64
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.sharing.shareFile
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual fun createPlatformImageSaver(): ImageSaver = UnsupportedAndroidImageSaver

actual fun createPlatformImageSharer(): ImageSharer = UnsupportedAndroidImageSharer

private object UnsupportedAndroidImageSaver : ImageSaver {
    override suspend fun save(base64: String): ImageSaveResult =
        ImageSaveResult.Unsupported
}

private object UnsupportedAndroidImageSharer : ImageSharer {
    override suspend fun share(base64: String): ImageShareResult =
        ImageShareResult.Unsupported
}

internal class AndroidImageSaver(
    private val mediaStoreGateway: MediaStoreGateway,
    private val dispatchersProvider: DispatchersProvider,
) : ImageSaver {

    override suspend fun save(base64: String): ImageSaveResult =
        runSavingAction {
            withContext(dispatchersProvider.io) {
                mediaStoreGateway.exportToFile(
                    fileName = createImageFileName(),
                    content = base64.toImageBytes(),
                )
            }
        }
}

internal class AndroidImageSharer(
    private val context: Context,
    override val fileProviderDescriptor: FileProviderDescriptor,
    private val dispatchersProvider: DispatchersProvider,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : ImageSharer, FileSavableExporter.BmpToFile {

    override suspend fun share(base64: String): ImageShareResult =
        runSharingAction {
            val file = withContext(dispatchersProvider.io) {
                val bitmap = base64ToBitmapConverter(Base64ToBitmapConverter.Input(base64)).bitmap
                saveBitmapToFile(createImageFileName(), bitmap)
            }
            withContext(dispatchersProvider.immediate) {
                context.shareFile(
                    file = file,
                    fileProviderPath = fileProviderDescriptor.providerPath,
                    fileMimeType = MIME_TYPE_JPG,
                )
            }
        }
}

private suspend inline fun runSavingAction(crossinline action: suspend () -> Unit): ImageSaveResult =
    try {
        action()
        ImageSaveResult.Saved
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        ImageSaveResult.Failed(t.message ?: "Unable to save image")
    }

private suspend inline fun runSharingAction(crossinline action: suspend () -> Unit): ImageShareResult =
    try {
        action()
        ImageShareResult.Sent
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        ImageShareResult.Failed(t.message ?: "Unable to share image")
    }

private fun String.toImageBytes(): ByteArray {
    val raw = substringAfter("base64,", this)
    return Base64.decode(raw, Base64.DEFAULT)
}

@OptIn(ExperimentalTime::class)
private fun createImageFileName(): String =
    "sdai_${Clock.System.now().toEpochMilliseconds()}"

private const val MIME_TYPE_JPG = "image/jpeg"
