package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.drawer.DrawerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.utils.Constants
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.Flow

class GalleryViewModel(
    getMediaStoreInfoUseCase: GetMediaStoreInfoUseCase,
    backgroundWorkObserver: BackgroundWorkObserver,
    preferenceManager: PreferenceManager,
    private val deleteAllGalleryUseCase: DeleteAllGalleryUseCase,
    private val deleteGalleryItemsUseCase: DeleteGalleryItemsUseCase,
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val galleryExporter: GalleryExporter,
    private val schedulersProvider: SchedulersProvider,
    private val mainRouter: MainRouter,
    private val drawerRouter: DrawerRouter,
) : MviRxViewModel<GalleryState, GalleryIntent, GalleryEffect>() {

    override val initialState = GalleryState()

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
        !preferenceManager
            .observe()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { settings ->
                updateState { it.copy(grid = settings.galleryGrid) }
            }

        !getMediaStoreInfoUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog) { info ->
                updateState { it.copy(mediaStoreInfo = info) }
            }

        !backgroundWorkObserver
            .observeResult()
            .ofType(BackgroundWorkResult.Success::class.java)
            .map { GalleryEffect.Refresh }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitEffect)
    }

    override fun processIntent(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.DismissDialog -> setActiveModal(Modal.None)

            GalleryIntent.Export.All.Request -> setActiveModal(Modal.ConfirmExport(true))

            GalleryIntent.Export.All.Confirm -> launchGalleryExport(true)

            GalleryIntent.Export.Selection.Request -> setActiveModal(Modal.ConfirmExport(false))

            GalleryIntent.Export.Selection.Confirm -> launchGalleryExport(false)

            is GalleryIntent.OpenItem -> mainRouter.navigateToGalleryDetails(intent.item.id)

            is GalleryIntent.OpenMediaStoreFolder -> emitEffect(GalleryEffect.OpenUri(intent.uri))

            is GalleryIntent.Drawer -> when (intent.intent) {
                DrawerIntent.Close -> drawerRouter.closeDrawer()
                DrawerIntent.Open -> drawerRouter.openDrawer()
            }

            is GalleryIntent.ChangeSelectionMode -> updateState {
                it.copy(
                    selectionMode = intent.flag,
                    selection = if (!intent.flag) emptyList() else it.selection,
                )
            }

            is GalleryIntent.ToggleItemSelection -> updateState {
                val selectionIndex = it.selection.indexOf(intent.id)
                val newSelection = it.selection.toMutableList()
                if (selectionIndex != -1) {
                    newSelection.removeAt(selectionIndex)
                } else {
                    newSelection.add(intent.id)
                }
                it.copy(selection = newSelection)
            }

            GalleryIntent.Delete.Selection.Request -> setActiveModal(
                Modal.DeleteImageConfirm(isAll = false, isMultiple = true)
            )

            GalleryIntent.Delete.Selection.Confirm -> !deleteGalleryItemsUseCase
                .invoke(currentState.selection)
                .processDeletion()

            GalleryIntent.Delete.All.Request -> setActiveModal(
                Modal.DeleteImageConfirm(isAll = true, isMultiple = false)
            )

            GalleryIntent.Delete.All.Confirm -> !deleteAllGalleryUseCase()
                .processDeletion()

            GalleryIntent.UnselectAll -> updateState {
                it.copy(selection = emptyList())
            }

            GalleryIntent.Dropdown.Toggle -> updateState {
                it.copy(dropdownMenuShow = !it.dropdownMenuShow)
            }

            GalleryIntent.Dropdown.Show -> updateState {
                it.copy(dropdownMenuShow = true)
            }

            GalleryIntent.Dropdown.Close -> updateState {
                it.copy(dropdownMenuShow = false)
            }
        }
    }

    private fun Completable.processDeletion() = this
        .doOnSubscribe { setActiveModal(Modal.None) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) {
            updateState {
                it.copy(
                    selectionMode = false,
                    selection = emptyList()
                )
            }
            emitEffect(GalleryEffect.Refresh)
        }

    private fun launchGalleryExport(exportAll: Boolean) = !galleryExporter
        .invoke(if (exportAll) null else currentState.selection)
        .doOnSubscribe { setActiveModal(Modal.ExportInProgress) }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                setActiveModal(
                    Modal.Error(
                        (t.localizedMessage ?: "Something went wrong").asUiText()
                    )
                )
                errorLog(t)
            },
            onSuccess = { zipFile ->
                setActiveModal(Modal.None)
                emitEffect(GalleryEffect.Share(zipFile))
            }
        )

    private fun setActiveModal(dialog: Modal) = updateState {
        it.copy(screenModal = dialog, dropdownMenuShow = false)
    }
}
