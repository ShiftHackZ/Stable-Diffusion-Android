package com.shifthackz.aisdv1.presentation.modal.history

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryPagingSource
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import com.shifthackz.android.core.mvi.EmptyState
import kotlinx.coroutines.flow.Flow

class InputHistoryViewModel(
    dispatchersProvider: DispatchersProvider,
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : MviRxViewModel<EmptyState, EmptyIntent, EmptyEffect>() {

    override val initialState = EmptyState

    override val effectDispatcher = dispatchersProvider.immediate

    private val config = PagingConfig(
        pageSize = Constants.PAGINATION_PAYLOAD_SIZE,
        initialLoadSize = Constants.PAGINATION_PAYLOAD_SIZE
    )

    private val pager: Pager<Int, InputHistoryItemUi> = Pager(
        config = config,
        initialKey = GalleryPagingSource.FIRST_KEY,
        pagingSourceFactory = {
            InputHistoryPagingSource(
                getGenerationResultPagedUseCase,
                base64ToBitmapConverter,
                schedulersProvider,
            )
        }
    )

    val pagingFlow: Flow<PagingData<InputHistoryItemUi>> = pager.flow
}
