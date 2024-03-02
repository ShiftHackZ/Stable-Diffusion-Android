package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class GalleryDetailViewModel(
    private val itemId: Long,
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    private val deleteGalleryItemUseCase: DeleteGalleryItemUseCase,
    private val galleryDetailBitmapExporter: GalleryDetailBitmapExporter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val mainRouter: MainRouter,
) : MviRxViewModel<GalleryDetailState, GalleryDetailIntent, GalleryDetailEffect>() {

    override val initialState = GalleryDetailState.Loading()

    init {
        !getGenerationResult(itemId)
            .subscribeOnMainThread(schedulersProvider)
            .postProcess()
            .subscribeBy(::errorLog) { ai ->
                updateState {
                    ai.mapToUi().withTab(currentState.selectedTab)
                }
            }
    }

    override fun processIntent(intent: GalleryDetailIntent) {
        when (intent) {
            is GalleryDetailIntent.CopyToClipboard -> {
                emitEffect(GalleryDetailEffect.ShareClipBoard(intent.content.toString()))
            }

            GalleryDetailIntent.Delete.Request -> {
                setActiveDialog(GalleryDetailState.Dialog.DeleteConfirm)
            }

            GalleryDetailIntent.Delete.Confirm -> {
                setActiveDialog(GalleryDetailState.Dialog.DeleteConfirm)
                delete()
            }

            GalleryDetailIntent.Export.Image -> share()

            GalleryDetailIntent.Export.Params -> {
                emitEffect(GalleryDetailEffect.ShareGenerationParams(currentState))
            }

            GalleryDetailIntent.NavigateBack -> mainRouter.navigateBack()

            is GalleryDetailIntent.SelectTab -> updateState {
                it.withTab(intent.tab)
            }

            GalleryDetailIntent.SendTo.Img2Img -> sendPromptToGenerationScreen(
                AiGenerationResult.Type.TEXT_TO_IMAGE,
            )

            GalleryDetailIntent.SendTo.Txt2Img -> sendPromptToGenerationScreen(
                AiGenerationResult.Type.IMAGE_TO_IMAGE,
            )

            GalleryDetailIntent.DismissDialog -> setActiveDialog(GalleryDetailState.Dialog.None)
        }

    }

    private fun share() {
        if (currentState !is GalleryDetailState.Content) return
        !galleryDetailBitmapExporter((currentState as GalleryDetailState.Content).bitmap)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { file ->
                emitEffect(GalleryDetailEffect.ShareImageFile(file))
            }
    }

    private fun delete() {
        if (currentState !is GalleryDetailState.Content) return
        !deleteGalleryItemUseCase((currentState as GalleryDetailState.Content).id)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { mainRouter.navigateBack() }
    }

    private fun setActiveDialog(dialog: GalleryDetailState.Dialog) = updateState {
        it.withDialog(dialog)
    }

    private fun Single<AiGenerationResult>.postProcess() = this
        .flatMap { ai ->
            base64ToBitmapConverter(Input(ai.image)).map { bmp -> ai to bmp }
        }
        .flatMap { (ai, bmp) ->
            when (ai.type) {
                AiGenerationResult.Type.TEXT_TO_IMAGE -> Single.just(Triple(ai, bmp, null))
                AiGenerationResult.Type.IMAGE_TO_IMAGE ->
                    base64ToBitmapConverter(Input(ai.inputImage)).map { bmp2 ->
                        Triple(ai, bmp, bmp2)
                    }
            }
        }

    private fun sendPromptToGenerationScreen(screenType: AiGenerationResult.Type) =
        !getGenerationResult(itemId)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { ai ->
                generationFormUpdateEvent.update(ai, screenType)
                mainRouter.navigateBack()
            }

    private fun getGenerationResult(id: Long): Single<AiGenerationResult> {
        if (id <= 0) return getLastResultFromCacheUseCase.invoke()
        return getGenerationResultUseCase(id)
    }
}
