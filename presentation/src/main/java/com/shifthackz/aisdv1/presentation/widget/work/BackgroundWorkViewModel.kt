package com.shifthackz.aisdv1.presentation.widget.work

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class BackgroundWorkViewModel(
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val schedulersProvider: SchedulersProvider,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : MviRxViewModel<BackgroundWorkState, BackgroundWorkIntent, EmptyEffect>() {

    override val initialState = BackgroundWorkState()

    init {
        !Flowable.combineLatest(
            backgroundWorkObserver.observeStatus(),
            backgroundWorkObserver.observeResult(),
            ::Pair,
        )
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { (work, result) ->
                updateState { state ->
                    val resultTitle = when (result) {
                        is BackgroundWorkResult.Error -> LocalizationR.string.notification_fail_title.asUiText()
                        is BackgroundWorkResult.Success -> LocalizationR.string.notification_finish_title.asUiText()
                        else -> UiText.empty
                    }
                    (result as? BackgroundWorkResult.Success)
                        ?.ai
                        ?.firstOrNull()
                        ?.image
                        ?.also(::setBitmap)
                    state.copy(
                        visible = work.running || result !is BackgroundWorkResult.None,
                        title = if (work.running) work.statusTitle.asUiText() else resultTitle,
                        subTitle = if (work.running) work.statusSubTitle.asUiText() else UiText.empty,
                        isError = !work.running && result is BackgroundWorkResult.Error,
                        bitmap = null,
                    )
                }
            }
    }

    override fun processIntent(intent: BackgroundWorkIntent) {
        when (intent) {
            BackgroundWorkIntent.Dismiss -> {
                updateState { it.copy(visible = false, isError = false, bitmap = null) }
                backgroundWorkObserver.dismissResult()
            }
        }
    }

    private fun setBitmap(base64: String) = !base64ToBitmapConverter(Base64ToBitmapConverter.Input(base64))
        .map(Base64ToBitmapConverter.Output::bitmap)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { bmp ->
            updateState { it.copy(bitmap = bmp) }
        }
}
