package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

typealias GalleryPagedResult = PagingSource.LoadResult<Int, GalleryItemUi>

class GalleryPagingSource private constructor(
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : RxPagingSource<Int, GalleryItemUi>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItemUi>) = FIRST_KEY

    override fun loadSingle(params: LoadParams<Int>) = loadSingleImpl(params)

    private fun loadSingleImpl(params: LoadParams<Int>): Single<GalleryPagedResult> {
        val pageSize = params.loadSize
        val pageNext = params.key ?: FIRST_KEY
        return getGenerationResultPagedUseCase(
            limit = pageSize,
            offset = pageNext * Constants.PAGINATION_PAYLOAD_SIZE,
        )
            .subscribeOn(schedulersProvider.io)
            .flatMapObservable { Observable.fromIterable(it) }
            .map { ai -> ai to ai.image }
            .map { (ai, base64) -> ai to Input(base64) }
            .concatMapSingle { (ai, input) ->
                base64ToBitmapConverter(input).map { out -> ai to out }
            }
            .map(::mapOutputToUi)
            .toList()
            .map { payload ->
                LoadResult.Page(
                    data = payload,
                    prevKey = if (pageNext == FIRST_KEY) null else pageNext - 1,
                    nextKey = if (payload.isEmpty()) null else pageNext + 1,
                ).let(GalleryPagingSource::Wrapper)
            }
            .onErrorReturn { t ->
                errorLog(t)
                Wrapper(LoadResult.Error(t))
            }
            .map(Wrapper::loadResult)
    }

    private fun mapOutputToUi(output: Pair<AiGenerationResult, Output>) = GalleryItemUi(
        output.first,
        output.second.bitmap,
    )

    private data class Wrapper(val loadResult: GalleryPagedResult)

    class Factory(
        private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
        private val base64ToBitmapConverter: Base64ToBitmapConverter,
        private val schedulersProvider: SchedulersProvider,
    ) {
        private val pager: Pager<Int, GalleryItemUi>  = Pager(
            config = PagingConfig(
                pageSize = Constants.PAGINATION_PAYLOAD_SIZE,
                initialLoadSize = Constants.PAGINATION_PAYLOAD_SIZE
            ),
            initialKey = FIRST_KEY,
            pagingSourceFactory = {
                GalleryPagingSource(
                    getGenerationResultPagedUseCase,
                    base64ToBitmapConverter,
                    schedulersProvider,
                )
            }
        )

        val pagingFlow: Flow<PagingData<GalleryItemUi>> = pager.flow
    }

    companion object {
        const val FIRST_KEY = 0
    }
}
