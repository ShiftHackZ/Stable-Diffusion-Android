package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

typealias GalleryPagedResult = PagingSource.LoadResult<Int, GalleryGridItemUi>

class GalleryPagingSource(
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : RxPagingSource<Int, GalleryGridItemUi>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryGridItemUi>) = FIRST_KEY

    override fun loadSingle(params: LoadParams<Int>) = loadSingleImpl(params)

    private fun loadSingleImpl(params: LoadParams<Int>): Single<GalleryPagedResult> {
        val pageSize = params.loadSize
        val pageNext = params.key ?: FIRST_KEY
        return getGenerationResultPagedUseCase(
            limit = pageSize,
            offset = pageNext * Constants.PAGINATION_PAYLOAD_SIZE,
        )
            .subscribeOn(schedulersProvider.computation)
            .flatMapObservable { Observable.fromIterable(it) }
            .map { ai -> Triple(ai.id, ai.hidden, ai.image) }
            .map { (id, hidden, base64) -> Triple(id, hidden, Input(base64)) }
            .concatMapSingle { (id, hidden, input) ->
                base64ToBitmapConverter(input).map { out -> Triple(id, hidden, out) }
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

    private fun mapOutputToUi(output: Triple<Long, Boolean, Output>) = GalleryGridItemUi(
        output.first,
        output.third.bitmap,
        output.second,
    )

    private data class Wrapper(val loadResult: GalleryPagedResult)

    companion object {
        const val FIRST_KEY = 0
    }
}
