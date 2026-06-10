package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.sharing.shareFile
import com.shifthackz.aisdv1.core.sharing.shareText
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaveResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Coordinates `AndroidGalleryDetailPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidGalleryDetailPlatformActions(
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
    /**
     * Exposes the `imageSaver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSaver: ImageSaver,
) : GalleryDetailPlatformActions, FileSavableExporter.BmpToFile {

    /**
     * Performs the SDAI side effect handled by `saveImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `saveImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSaver.save(base64)) {
            ImageSaveResult.Saved -> GalleryDetailActionResult.Done
            ImageSaveResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageSaveResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `shareImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareImage(base64: String): GalleryDetailActionResult =
        runPlatformAction {
            val file = withContext(dispatchersProvider.io) {
                val bitmap = base64ToBitmapConverter(Input(base64)).bitmap
                saveBitmapToFile(System.currentTimeMillis().toString(), bitmap)
            }
            withContext(dispatchersProvider.immediate) {
                context.shareFile(
                    file = file,
                    fileProviderPath = fileProviderDescriptor.providerPath,
                    fileMimeType = MIME_TYPE_JPG,
                )
            }
        }

    /**
     * Performs the SDAI side effect handled by `shareText`.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `shareText`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareText(text: String): GalleryDetailActionResult =
        runPlatformAction {
            withContext(dispatchersProvider.immediate) {
                context.shareText(text)
            }
        }

    /**
     * Executes the `copyText` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `copyText`.
     * @author Dmitriy Moroz
     */
    override suspend fun copyText(text: String): GalleryDetailActionResult =
        runPlatformAction {
            withContext(dispatchersProvider.immediate) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
            }
        }

    /**
     * Executes the `runPlatformAction` step in the SDAI presentation layer.
     *
     * @param action action value consumed by the API.
     * @return Result produced by `runPlatformAction`.
     * @author Dmitriy Moroz
     */
    private suspend inline fun runPlatformAction(crossinline action: suspend () -> Unit): GalleryDetailActionResult =
        try {
            action()
            GalleryDetailActionResult.Done
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            GalleryDetailActionResult.Failed(t.message ?: "Unable to complete action")
        }

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `CLIP_LABEL` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        const val CLIP_LABEL = "SDAI gallery detail"
        /**
         * Exposes the `MIME_TYPE_JPG` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        const val MIME_TYPE_JPG = "image/jpeg"
    }
}
