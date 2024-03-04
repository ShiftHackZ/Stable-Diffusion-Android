package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject

class GenerationFormUpdateEvent {

    private val sRoute: PublishSubject<AiGenerationResult.Type> = PublishSubject.create()
    private val sTxt2Img: PublishSubject<AiGenerationResult> = PublishSubject.create()
    private val sImg2Img: PublishSubject<AiGenerationResult> = PublishSubject.create()

    fun update(generation: AiGenerationResult, route: AiGenerationResult.Type) {
        sRoute.onNext(route)
        when (route) {
            AiGenerationResult.Type.TEXT_TO_IMAGE -> sTxt2Img.onNext(generation)
            AiGenerationResult.Type.IMAGE_TO_IMAGE -> sImg2Img.onNext(generation)
        }
    }

    fun observeRoute() = sRoute.toFlowable(BackpressureStrategy.LATEST)

    fun observeTxt2ImgForm() = sTxt2Img.toFlowable(BackpressureStrategy.LATEST)

    fun observeImg2ImgForm() = sImg2Img.toFlowable(BackpressureStrategy.LATEST)
}
