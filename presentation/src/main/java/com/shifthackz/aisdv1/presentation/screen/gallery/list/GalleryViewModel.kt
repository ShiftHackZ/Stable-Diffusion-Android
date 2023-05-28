package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.paging.PagingData
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(
//    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
//    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val pagingFactory: GalleryPagingSource.Factory,
    private val galleryExporter: GalleryExporter,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<GalleryState, GalleryEffect>() {

    override val emptyState = GalleryState()

//    private val config = PagingConfig(
//        pageSize = Constants.PAGINATION_PAYLOAD_SIZE,
//        initialLoadSize = Constants.PAGINATION_PAYLOAD_SIZE
//    )
//
//    private val pager: Pager<Int, GalleryItemUi> = Pager(
//        config = config,
//        initialKey = GalleryPagingSource.FIRST_KEY,
//        pagingSourceFactory = {
//            GalleryPagingSource(
//                getGenerationResultPagedUseCase,
//                base64ToBitmapConverter,
//                schedulersProvider,
//            )
//        }
//    )

    val pagingFlow: Flow<PagingData<GalleryItemUi>> = pagingFactory.pagingFlow

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
