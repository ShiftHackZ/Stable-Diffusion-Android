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

internal class AndroidGalleryDetailPlatformActions(
    private val context: Context,
    override val fileProviderDescriptor: FileProviderDescriptor,
    private val dispatchersProvider: DispatchersProvider,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val imageSaver: ImageSaver,
) : GalleryDetailPlatformActions, FileSavableExporter.BmpToFile {

    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSaver.save(base64)) {
            ImageSaveResult.Saved -> GalleryDetailActionResult.Done
            ImageSaveResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageSaveResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

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

    override suspend fun shareText(text: String): GalleryDetailActionResult =
        runPlatformAction {
            withContext(dispatchersProvider.immediate) {
                context.shareText(text)
            }
        }

    override suspend fun copyText(text: String): GalleryDetailActionResult =
        runPlatformAction {
            withContext(dispatchersProvider.immediate) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, text))
            }
        }

    private suspend inline fun runPlatformAction(crossinline action: suspend () -> Unit): GalleryDetailActionResult =
        try {
            action()
            GalleryDetailActionResult.Done
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            GalleryDetailActionResult.Failed(t.message ?: "Unable to complete action")
        }

    private companion object {
        const val CLIP_LABEL = "SDAI gallery detail"
        const val MIME_TYPE_JPG = "image/jpeg"
    }
}
