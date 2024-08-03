package com.shifthackz.aisdv1.data.core

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.ByteArrayOutputStream

internal abstract class CoreMediaStoreRepository(
    private val preferenceManager: PreferenceManager,
    private val mediaStoreGateway: MediaStoreGateway,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) {

    protected fun exportToMediaStore(result: AiGenerationResult): Completable {
        if (preferenceManager.saveToMediaStore) return export(result)
        return Completable.complete()
    }

    protected fun getInfo(): Single<MediaStoreInfo> = Single.create { emitter ->
        emitter.onSuccess(mediaStoreGateway.getInfo())
    }

    private fun export(result: AiGenerationResult) = result.image
        .let(Base64ToBitmapConverter::Input)
        .let(base64ToBitmapConverter::invoke)
        .map(Base64ToBitmapConverter.Output::bitmap)
        .flatMapCompletable(::processBitmap)

    private fun processBitmap(bmp: Bitmap) = Completable
        .fromAction {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            mediaStoreGateway.exportToFile(
                fileName = "sdai_${System.currentTimeMillis()}",
                content = stream.toByteArray(),
            )
        }
        .onErrorComplete { t ->
            errorLog(t)
            true
        }
}
