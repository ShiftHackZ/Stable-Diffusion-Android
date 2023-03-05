package com.shifthackz.aisdv1.presentation.screen.gallery

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.writeBitmap
import com.shifthackz.aisdv1.core.common.file.writeFilesToZip
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapProcessor
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

class GalleryExporter(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapProcessor,
    private val schedulersProvider: SchedulersProvider,
) {

    operator fun invoke(): Completable = getAllGalleryUseCase()
        .subscribeOn(schedulersProvider.io)
        .flatMapObservable { Observable.fromIterable(it) }
        .map { aiDomain -> aiDomain to Input(aiDomain.image) }
        .flatMapSingle { (aiDomain, input) ->
            base64ToBitmapConverter(input).map { out -> aiDomain to out }
        }
        .flatMapSingle(::saveBitmapToFile)
        .toList()
        .flatMap(::saveFilesToZip)
        .ignoreElement()

    private fun saveBitmapToFile(data: Pair<AiGenerationResultDomain, Output>) = Single
        .create { emitter ->
            val (ai, out) = data
            val bitmap = out.bitmap
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "ai_${ai.hashCode()}.jpg")
            outFile.writeBitmap(bitmap)
            emitter.onSuccess(outFile)
        }

    private fun saveFilesToZip(files: List<File>) = Single.create { emitter ->
        val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
        val outFile = File(cacheDirectory, "export_${System.currentTimeMillis()}.zip")
        outFile.writeFilesToZip(files)
        emitter.onSuccess(outFile)
    }
}
