package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(
    private val getMediaStoreInfoUseCase: GetMediaStoreInfoUseCase,
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val galleryExporter: GalleryExporter,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<GalleryState, GalleryEffect>() {

    override val emptyState = GalleryState()

    private val config = PagingConfig(
        pageSize = Constants.PAGINATION_PAYLOAD_SIZE,
        initialLoadSize = Constants.PAGINATION_PAYLOAD_SIZE
    )

    private val pager: Pager<Int, GalleryGridItemUi> = Pager(
        config = config,
        initialKey = GalleryPagingSource.FIRST_KEY,
        pagingSourceFactory = {
            GalleryPagingSource(
                getGenerationResultPagedUseCase,
                base64ToBitmapConverter,
                schedulersProvider,
            )
        }
    )

    val pagingFlow: Flow<PagingData<GalleryGridItemUi>> = pager.flow

    init {
        !getMediaStoreInfoUseCase()
            .map { info -> currentState.copy(mediaStoreInfo = info) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, ::setState)
    }

    fun dismissScreenDialog() = setActiveDialog(GalleryState.Dialog.None)

    fun launchGalleryExportConfirmation() = setActiveDialog(GalleryState.Dialog.ConfirmExport)

    fun launchGalleryExport() = galleryExporter()
        .doOnSubscribe { setActiveDialog(GalleryState.Dialog.ExportInProgress) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                setActiveDialog(
                    GalleryState.Dialog.Error(
                        (t.localizedMessage ?: "Something went wrong").asUiText()
                    )
                )
                errorLog(t)
            },
            onSuccess = { zipFile ->
                setActiveDialog(GalleryState.Dialog.None)
                emitEffect(GalleryEffect.Share(zipFile))
            }
        )
        .addToDisposable()

    private fun setActiveDialog(dialog: GalleryState.Dialog) = currentState
        .copy(screenDialog = dialog)
        .let(::setState)
}
