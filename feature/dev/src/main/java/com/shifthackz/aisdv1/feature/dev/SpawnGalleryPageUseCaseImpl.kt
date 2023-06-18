package com.shifthackz.aisdv1.feature.dev

import com.shifthackz.aisdv1.core.common.extensions.randomColorBitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import com.shifthackz.aisdv1.domain.usecase.dev.SpawnGalleryPageUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

internal class SpawnGalleryPageUseCaseImpl(
    private val repository: GenerationResultRepository,
    private val imageToBase64Converter: BitmapToBase64Converter,
) : SpawnGalleryPageUseCase {

    override operator fun invoke(amount: Int): Completable = Observable
        .fromIterable((0 until amount).map { randomColorBitmap(512, 512) })
        .map(BitmapToBase64Converter::Input)
        .flatMapSingle(imageToBase64Converter::invoke)
        .map(BitmapToBase64Converter.Output::base64ImageString)
        .map { base64 -> AiGenerationResult.empty().copy(image = base64) }
        .toList()
        .flatMapCompletable(repository::insert)
}
