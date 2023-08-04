package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.FeatureFlags
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.features.GetFeatureFlagsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.features.GalleryDetailTabClick
import com.shifthackz.aisdv1.presentation.features.GalleryItemDelete
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class GalleryDetailViewModel(
    getFeatureFlagsUseCase: GetFeatureFlagsUseCase,
    private val itemId: Long,
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    private val deleteGalleryItemUseCase: DeleteGalleryItemUseCase,
    private val galleryDetailBitmapExporter: GalleryDetailBitmapExporter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val analytics: Analytics,
) : MviRxViewModel<GalleryDetailState, GalleryDetailEffect>() {

    override val emptyState = GalleryDetailState.Loading()

    init {
        !Single.zip(
            getFeatureFlagsUseCase(),
            getGenerationResult(itemId),
            ::Pair,
        )
            .subscribeOnMainThread(schedulersProvider)
            .postProcess()
            .subscribeBy(::errorLog) { (ff, ai) ->
                ai
                    .mapToUi()
                    .withTab(currentState.selectedTab)
                    .withBanner(ff.adGalleryBottomEnable)
                    .let(::setState)
            }
    }

    fun sendPromptToTxt2Img() = sendPromptToGenerationScreen(
        AiGenerationResult.Type.TEXT_TO_IMAGE,
    )

    fun sendPromptToImg2Img() = sendPromptToGenerationScreen(
        AiGenerationResult.Type.IMAGE_TO_IMAGE,
    )

    fun selectTab(tab: GalleryDetailState.Tab) = currentState
        .withTab(tab)
        .let(::setState)
        .also { analytics.logEvent(GalleryDetailTabClick(tab)) }

    fun showDeleteConfirmDialog() = setActiveDialog(GalleryDetailState.Dialog.DeleteConfirm)

    fun dismissScreenDialog() = setActiveDialog(GalleryDetailState.Dialog.None)

    fun share() {
        if (currentState !is GalleryDetailState.Content) return
        !galleryDetailBitmapExporter((currentState as GalleryDetailState.Content).bitmap)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { file ->
                emitEffect(GalleryDetailEffect.ShareImageFile(file))
            }
    }

    fun delete() {
        dismissScreenDialog()
        if (currentState !is GalleryDetailState.Content) return
        analytics.logEvent(GalleryItemDelete)
        !deleteGalleryItemUseCase((currentState as GalleryDetailState.Content).id)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { emitEffect(GalleryDetailEffect.NavigateBack) }
    }

    private fun setActiveDialog(dialog: GalleryDetailState.Dialog) = currentState
        .withDialog(dialog)
        .let(::setState)

    private fun Single<Pair<FeatureFlags, AiGenerationResult>>.postProcess() = this
        .flatMap { (ff, ai) ->
            base64ToBitmapConverter(Input(ai.image)).map { bmp -> ff to (ai to bmp) }
        }
        .flatMap { (ff, data) ->
            val (ai, bmp) = data
            when (ai.type) {
                AiGenerationResult.Type.TEXT_TO_IMAGE -> Single.just(ff to Triple(ai, bmp, null))
                AiGenerationResult.Type.IMAGE_TO_IMAGE ->
                    base64ToBitmapConverter(Input(ai.inputImage)).map { bmp2 ->
                        ff to Triple(ai, bmp, bmp2)
                    }
            }
        }

    private fun sendPromptToGenerationScreen(screenType: AiGenerationResult.Type) =
        !getGenerationResult(itemId)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { ai ->
                generationFormUpdateEvent.update(ai, screenType)
                emitEffect(GalleryDetailEffect.NavigateBack)
            }

    private fun getGenerationResult(id: Long): Single<AiGenerationResult> {
        if (id <= 0) return getLastResultFromCacheUseCase.invoke()
        return getGenerationResultUseCase(id)
    }
}
