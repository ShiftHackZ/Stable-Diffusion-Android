package com.shifthackz.aisdv1.presentation.screen.gallery.v2

import androidx.paging.PagingData
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultIdsUseCase
import com.shifthackz.aisdv1.presentation.features.GalleryDetailTabClick
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryItemUi
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryPagingSource
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.Flow

class GallerySlideViewModel(
    itemId: Long,
//    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
//
    private val getGenerationResultIdsUseCase: GetGenerationResultIdsUseCase,
//
//    private val getGenerationResultUseCase: GetGenerationResultUseCase,
//
    private val pagingFactory: GalleryPagingSource.Factory,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
    private val analytics: Analytics,
) : MviRxViewModel<GalleryStateV2, GalleryDetailEffect>() {

    override val emptyState = GalleryStateV2.Uninitialized

    val pagingFlow: Flow<PagingData<GalleryItemUi>> = pagingFactory.pagingFlow

    init {
        !getGenerationResultIdsUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { ids ->
                val number = ids.indexOf(itemId)


                setState(GalleryStateV2.Initialized( keys = ids,totalPageCount = ids.size, initialIndex = number))
            }
    }

    fun selectTab(tab: GalleryTab) = (currentState as? GalleryStateV2.Initialized)
        ?.copy(selectedTab = tab)
        ?.let(::setState)
        ?.also { analytics.logEvent(GalleryDetailTabClick(tab)) }
}
