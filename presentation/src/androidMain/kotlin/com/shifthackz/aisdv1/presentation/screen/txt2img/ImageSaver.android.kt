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

/**
 * Creates the SDAI value produced by `createPlatformImageSaver`.
 *
 * @return Result produced by `createPlatformImageSaver`.
 * @author Dmitriy Moroz
 */
actual fun createPlatformImageSaver(): ImageSaver = UnsupportedAndroidImageSaver

/**
 * Creates the SDAI value produced by `createPlatformImageSharer`.
 *
 * @return Result produced by `createPlatformImageSharer`.
 * @author Dmitriy Moroz
 */
actual fun createPlatformImageSharer(): ImageSharer = UnsupportedAndroidImageSharer

/**
 * Provides the `UnsupportedAndroidImageSaver` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private object UnsupportedAndroidImageSaver : ImageSaver {
    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `save`.
     * @author Dmitriy Moroz
     */
    override suspend fun save(base64: String): ImageSaveResult =
        ImageSaveResult.Unsupported
}

/**
 * Provides the `UnsupportedAndroidImageSharer` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private object UnsupportedAndroidImageSharer : ImageSharer {
    /**
     * Performs the SDAI side effect handled by `share`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `share`.
     * @author Dmitriy Moroz
     */
    override suspend fun share(base64: String): ImageShareResult =
        ImageShareResult.Unsupported
}

/**
 * Coordinates `AndroidImageSaver` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidImageSaver(
    /**
     * Exposes the `mediaStoreGateway` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val mediaStoreGateway: MediaStoreGateway,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
) : ImageSaver {

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `save`.
     * @author Dmitriy Moroz
     */
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

/**
 * Coordinates `AndroidImageSharer` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidImageSharer(
    /**
     * Exposes the `context` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `base64ToBitmapConverter` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : ImageSharer, FileSavableExporter.BmpToFile {

    /**
     * Performs the SDAI side effect handled by `share`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `share`.
     * @author Dmitriy Moroz
     */
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

/**
 * Executes the `runSavingAction` step in the SDAI presentation layer.
 *
 * @param action action value consumed by the API.
 * @return Result produced by `runSavingAction`.
 * @author Dmitriy Moroz
 */
private suspend inline fun runSavingAction(crossinline action: suspend () -> Unit): ImageSaveResult =
    try {
        action()
        ImageSaveResult.Saved
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        ImageSaveResult.Failed(t.message ?: "Unable to save image")
    }

/**
 * Executes the `runSharingAction` step in the SDAI presentation layer.
 *
 * @param action action value consumed by the API.
 * @return Result produced by `runSharingAction`.
 * @author Dmitriy Moroz
 */
private suspend inline fun runSharingAction(crossinline action: suspend () -> Unit): ImageShareResult =
    try {
        action()
        ImageShareResult.Sent
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        ImageShareResult.Failed(t.message ?: "Unable to share image")
    }

/**
 * Converts SDAI data with `toImageBytes`.
 *
 * @return Result produced by `toImageBytes`.
 * @author Dmitriy Moroz
 */
private fun String.toImageBytes(): ByteArray {
    val raw = substringAfter("base64,", this)
    return Base64.decode(raw, Base64.DEFAULT)
}

/**
 * Creates the SDAI value produced by `createImageFileName`.
 *
 * @return Result produced by `createImageFileName`.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalTime::class)
private fun createImageFileName(): String =
    "sdai_${Clock.System.now().toEpochMilliseconds()}"

/**
 * Exposes the `MIME_TYPE_JPG` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private const val MIME_TYPE_JPG = "image/jpeg"
