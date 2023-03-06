package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapProcessor
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

class GalleryExporter(
    override val fileProviderDescriptor: FileProviderDescriptor,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapProcessor,
    private val schedulersProvider: SchedulersProvider,
) : FileSavableExporter.BmpToFile, FileSavableExporter.FilesToZip {

    operator fun invoke(): Single<File> = getAllGalleryUseCase()
        .subscribeOn(schedulersProvider.io)
        .flatMapObservable { Observable.fromIterable(it) }
        .map { aiDomain -> aiDomain to Input(aiDomain.image) }
        .flatMapSingle { (aiDomain, input) ->
            base64ToBitmapConverter(input).map { out -> aiDomain to out }
        }
        .flatMapSingle(::saveBitmapToFileImpl)
        .toList()
        .flatMap(::saveFilesToZip)

    private fun saveBitmapToFileImpl(data: Pair<AiGenerationResult, Output>) =
        saveBitmapToFile(data.first.hashCode().toString(), data.second.bitmap)
}
