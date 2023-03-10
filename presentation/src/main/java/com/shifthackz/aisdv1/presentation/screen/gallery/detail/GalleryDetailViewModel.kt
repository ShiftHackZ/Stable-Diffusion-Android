package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemUseCase
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class GalleryDetailViewModel(
    itemId: Long,
    getGalleryItemUseCase: GetGalleryItemUseCase,
    private val deleteGalleryItemUseCase: DeleteGalleryItemUseCase,
    private val galleryDetailBitmapExporter: GalleryDetailBitmapExporter,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<GalleryDetailState, GalleryDetailEffect>() {

    override val emptyState = GalleryDetailState.Loading()

    init {
        !getGalleryItemUseCase(itemId)
            .subscribeOnMainThread(schedulersProvider)
            .postProcess()
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = { aiData ->
                    aiData
                        .mapToUi()
                        .withTab(currentState.selectedTab)
                        .let(::setState)
                },
            )
    }

    fun selectTab(tab: GalleryDetailState.Tab) = currentState.withTab(tab).let(::setState)

    fun showDeleteConfirmDialog() = setActiveDialog(GalleryDetailState.Dialog.DeleteConfirm)

    fun dismissScreenDialog() = setActiveDialog(GalleryDetailState.Dialog.None)

    fun share() {
        if (currentState !is GalleryDetailState.Content) return
        !galleryDetailBitmapExporter((currentState as GalleryDetailState.Content).bitmap)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = {

                },
                onSuccess = { file ->
                    emitEffect(GalleryDetailEffect.ShareImageFile(file))
                }
            )
    }

    fun delete() {
        dismissScreenDialog()
        if (currentState !is GalleryDetailState.Content) return
        !deleteGalleryItemUseCase((currentState as GalleryDetailState.Content).id)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t -> t.printStackTrace() },
                onComplete = { emitEffect(GalleryDetailEffect.NavigateBack) },
            )
    }

    private fun setActiveDialog(dialog: GalleryDetailState.Dialog) = currentState
        .withDialog(dialog)
        .let(::setState)

    private fun Single<AiGenerationResult>.postProcess() = this
        .flatMap { ai ->
            base64ToBitmapConverter(Input(ai.image)).map { bmp -> ai to bmp }
        }
        .flatMap { (ai, bmp) ->
            when (ai.type) {
                AiGenerationResult.Type.TEXT_TO_IMAGE -> Single.just(Triple(ai, bmp, null))
                AiGenerationResult.Type.IMAGE_TO_IMAGE ->
                    base64ToBitmapConverter(Input(ai.inputImage)).map { bmp2 ->
                        Triple(
                            ai,
                            bmp,
                            bmp2,
                        )
                    }
            }
        }
}
