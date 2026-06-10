package com.shifthackz.aisdv1.presentation.screen.img2img

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.errorLog
import java.io.File
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
actual fun rememberImageToImagePlatformActions(): ImageToImagePlatformActions {
    val context = LocalContext.current
    val fileProviderDescriptor = koinInject<FileProviderDescriptor>()
    val pickerState = remember { AndroidImagePickerState() }
    val cameraFile = remember(context) {
        File(context.cacheDir, "img2img-camera.jpg").apply {
            createNewFile()
        }
    }
    val cameraUri = remember(context, fileProviderDescriptor, cameraFile) {
        FileProvider.getUriForFile(
            context,
            fileProviderDescriptor.providerPath,
            cameraFile,
        )
    }

    val cameraPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            pickerState.resume(AndroidImagePickOutcome.Selected(cameraUri))
        } else {
            pickerState.resume(AndroidImagePickOutcome.Cancelled)
        }
    }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            cameraPicker.launch(cameraUri)
        } else {
            pickerState.resume(AndroidImagePickOutcome.Failed("Camera permission was not granted"))
        }
    }

    val mediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri == null) {
            pickerState.resume(AndroidImagePickOutcome.Cancelled)
        } else {
            pickerState.resume(AndroidImagePickOutcome.Selected(uri))
        }
    }

    return remember(context, pickerState, cameraPicker, cameraPermission, mediaPicker) {
        object : ImageToImagePlatformActions {
            override suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult =
                when (val outcome = pickerState.await {
                    when (source) {
                        ImageToImagePickSource.Camera -> {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA,
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                cameraPicker.launch(cameraUri)
                            } else {
                                cameraPermission.launch(Manifest.permission.CAMERA)
                            }
                        }

                        ImageToImagePickSource.Gallery -> {
                            val request = PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            )
                            mediaPicker.launch(request)
                        }
                    }
                }) {
                    AndroidImagePickOutcome.Cancelled -> ImageToImagePickResult.Cancelled
                    is AndroidImagePickOutcome.Failed -> ImageToImagePickResult.Failed(outcome.message)
                    is AndroidImagePickOutcome.Selected -> withContext(Dispatchers.Default) {
                        context.imageUriToPickResult(outcome.uri)
                    }
                }
        }
    }
}

private sealed interface AndroidImagePickOutcome {
    data class Selected(val uri: Uri) : AndroidImagePickOutcome
    data object Cancelled : AndroidImagePickOutcome
    data class Failed(val message: String) : AndroidImagePickOutcome
}

private class AndroidImagePickerState {
    private var continuation: CancellableContinuation<AndroidImagePickOutcome>? = null

    suspend fun await(launchPicker: () -> Unit): AndroidImagePickOutcome =
        suspendCancellableCoroutine { nextContinuation ->
            if (continuation != null) {
                nextContinuation.resume(
                    AndroidImagePickOutcome.Failed("Another image picker is already active"),
                )
                return@suspendCancellableCoroutine
            }
            continuation = nextContinuation
            nextContinuation.invokeOnCancellation {
                if (continuation == nextContinuation) {
                    continuation = null
                }
            }
            launchPicker()
        }

    fun resume(result: AndroidImagePickOutcome) {
        val current = continuation ?: return
        continuation = null
        current.resume(result)
    }
}

private fun Context.imageUriToPickResult(uri: Uri): ImageToImagePickResult =
    runCatching {
        val bitmap = decodeSampledBitmap(uri) ?: error("Unable to decode selected image")
        val normalized = bitmap.centerCropAndScale()
        if (normalized !== bitmap) {
            bitmap.recycle()
        }
        ImageToImagePickResult.Selected(normalized.toJpegBase64())
    }.getOrElse { t ->
        ImageToImagePickResult.Failed(t.message ?: "Unable to read selected image")
    }

private fun Context.decodeSampledBitmap(uri: Uri): Bitmap? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }
        if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

        val decodeOptions = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inSampleSize = boundsOptions.calculateInSampleSize(MAX_PICKED_IMAGE_SIDE * 2)
        }
        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    } catch (e: Exception) {
        errorLog("ImageToImagePlatformActions", e)
        null
    }
}

private fun BitmapFactory.Options.calculateInSampleSize(maxSide: Int): Int {
    var sampleSize = 1
    while (maxOf(outWidth / sampleSize, outHeight / sampleSize) > maxSide) {
        sampleSize *= 2
    }
    return sampleSize.coerceAtLeast(1)
}

private fun Bitmap.centerCropAndScale(): Bitmap {
    val cropSize = minOf(width, height)
    val cropped = if (width == cropSize && height == cropSize) {
        this
    } else {
        Bitmap.createBitmap(
            this,
            (width - cropSize) / 2,
            (height - cropSize) / 2,
            cropSize,
            cropSize,
        )
    }
    if (cropSize <= MAX_PICKED_IMAGE_SIDE) return cropped

    return Bitmap
        .createScaledBitmap(cropped, MAX_PICKED_IMAGE_SIDE, MAX_PICKED_IMAGE_SIDE, true)
        .also {
            if (cropped !== this) {
                cropped.recycle()
            }
        }
}

private fun Bitmap.toJpegBase64(): String =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, PICKED_IMAGE_JPEG_QUALITY, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

private const val MAX_PICKED_IMAGE_SIDE = 1536
private const val PICKED_IMAGE_JPEG_QUALITY = 95
