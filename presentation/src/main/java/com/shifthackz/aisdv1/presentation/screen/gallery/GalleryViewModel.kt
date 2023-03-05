package com.shifthackz.aisdv1.presentation.screen.gallery

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.contract.RxImageProcessor
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryPageUseCase
import com.shifthackz.aisdv1.presentation.utils.Constants
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(
    private val getGalleryPageUseCase: GetGalleryPageUseCase,
    private val base64ToBitmapConverter: RxImageProcessor<Input, Output>,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<GalleryState, EmptyEffect>() {

    override val emptyState = GalleryState.Paginated()

    private val config = PagingConfig(
        pageSize = Constants.PAGINATION_PAYLOAD_SIZE,
        initialLoadSize = Constants.PAGINATION_PAYLOAD_SIZE
    )

    private val pager: Pager<Int, GalleryGridItemUi>

    val pagingFlow: Flow<PagingData<GalleryGridItemUi>>
        get() = pager.flow.cachedIn(viewModelScope)

    init {
        pager = Pager(
            config = config,
            initialKey = GalleryPagingSource.FIRST_KEY,
            pagingSourceFactory = {
                GalleryPagingSource(
                    getGalleryPageUseCase,
                    base64ToBitmapConverter,
                    schedulersProvider,
                )
            }
        )
    }
}
