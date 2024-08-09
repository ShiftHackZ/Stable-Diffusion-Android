package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemsUseCase
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.File

class GalleryExporter(
    override val fileProviderDescriptor: FileProviderDescriptor,
    private val getGalleryItemsUseCase: GetGalleryItemsUseCase,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : FileSavableExporter.BmpToFile, FileSavableExporter.FilesToZip {

    operator fun invoke(ids: List<Long>? = null): Single<File> {
        val chain = ids?.let(getGalleryItemsUseCase::invoke) ?: getAllGalleryUseCase()
        return chain
            .subscribeOn(schedulersProvider.io)
            .flatMapObservable { Observable.fromIterable(it) }
            .map { aiDomain -> aiDomain to Input(aiDomain.image) }
            .flatMapSingle { (aiDomain, input) ->
                base64ToBitmapConverter(input).map { out -> aiDomain to out }
            }
            .flatMapSingle(::saveBitmapToFileImpl)
            .toList()
            .flatMap(::saveFilesToZip)
    }

    private fun saveBitmapToFileImpl(data: Pair<AiGenerationResult, Output>) =
        saveBitmapToFile(data.first.hashCode().toString(), data.second.bitmap)
}
