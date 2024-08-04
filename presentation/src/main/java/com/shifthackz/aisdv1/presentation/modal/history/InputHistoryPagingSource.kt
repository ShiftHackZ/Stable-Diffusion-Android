package com.shifthackz.aisdv1.presentation.modal.history

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

typealias InputHistoryPagedResult = PagingSource.LoadResult<Int, InputHistoryItemUi>

class InputHistoryPagingSource(
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val schedulersProvider: SchedulersProvider,
) : RxPagingSource<Int, InputHistoryItemUi>() {

    override fun getRefreshKey(state: PagingState<Int, InputHistoryItemUi>): Int = FIRST_KEY

    override fun loadSingle(params: LoadParams<Int>): Single<InputHistoryPagedResult> = loadSingleImpl(params)

    private fun loadSingleImpl(params: LoadParams<Int>): Single<InputHistoryPagedResult> {
        val pageSize = params.loadSize
        val pageNext = params.key ?: FIRST_KEY
        return getGenerationResultPagedUseCase(
            limit = pageSize,
            offset = pageNext * Constants.PAGINATION_PAYLOAD_SIZE,
        )
            .subscribeOn(schedulersProvider.io)
            .flatMapObservable { Observable.fromIterable(it) }
            .map { ai -> ai to Input(ai.image) }
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
                ).let(::Wrapper)
            }
            .onErrorReturn { t ->
                errorLog(t)
                Wrapper(LoadResult.Error(t))
            }
            .map(Wrapper::loadResult)
    }

    private fun mapOutputToUi(output: Pair<AiGenerationResult, Output>): InputHistoryItemUi = InputHistoryItemUi(
        output.first,
        output.second.bitmap,
    )

    private data class Wrapper(val loadResult: InputHistoryPagedResult)

    companion object {
        const val FIRST_KEY = 0
    }
}
