package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemsUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetMediaStoreInfoUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultPagedUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow

private const val GALLERY_FIRST_PAGE = 0
private const val GALLERY_PAGE_SIZE = 60

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class GalleryViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val getMediaStoreInfoUseCase: GetMediaStoreInfoUseCase,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val deleteAllGalleryUseCase: DeleteAllGalleryUseCase,
    private val deleteGalleryItemsUseCase: DeleteGalleryItemsUseCase,
    private val getGenerationResultPagedUseCase: GetGenerationResultPagedUseCase,
    private val galleryExportService: GalleryExportService,
    private val galleryRouter: GalleryRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<GalleryState, GalleryIntent, GalleryEffect>(
    initialState = GalleryState(grid = preferenceManager.galleryGrid),
    effectDispatcher = dispatchersProvider.immediate,
) {
    private val requestedLimit = MutableStateFlow(GALLERY_PAGE_SIZE)

    init {
        observePreferences()
        observeBackgroundWork()
        observeGallery()
        loadMediaStoreInfo()
    }

    override fun processIntent(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.DismissDialog -> setActiveDialog(GalleryDialog.None)

            GalleryIntent.LoadNextPage -> loadNextPage()

            GalleryIntent.Export.All.Request -> setActiveDialog(GalleryDialog.ConfirmExport(true))

            GalleryIntent.Export.All.Confirm -> launchGalleryExport(exportAll = true)

            GalleryIntent.Export.Selection.Request -> setActiveDialog(GalleryDialog.ConfirmExport(false))

            GalleryIntent.Export.Selection.Confirm -> launchGalleryExport(exportAll = false)

            is GalleryIntent.OpenItem -> galleryRouter.navigateToGalleryDetails(intent.id)

            GalleryIntent.OpenDrawer -> galleryRouter.openDrawer()

            GalleryIntent.OpenMediaStoreFolder -> currentState.mediaStoreInfo.folderUri
                ?.let { emitEffect(GalleryEffect.OpenMediaStoreFolder(it)) }

            is GalleryIntent.ChangeSelectionMode -> updateState {
                it.copy(
                    selectionMode = intent.flag,
                    selection = if (!intent.flag) emptyList() else it.selection,
                )
            }

            is GalleryIntent.ToggleItemSelection -> updateState {
                val newSelection = it.selection.toMutableList()
                if (intent.id in newSelection) newSelection.remove(intent.id)
                else newSelection.add(intent.id)
                it.copy(selection = newSelection)
            }

            GalleryIntent.Delete.Selection.Request -> setActiveDialog(GalleryDialog.DeleteSelectionConfirm)

            GalleryIntent.Delete.Selection.Confirm -> launchDeletion {
                deleteGalleryItemsUseCase(currentState.selection)
            }

            GalleryIntent.Delete.All.Request -> setActiveDialog(GalleryDialog.DeleteAllConfirm)

            GalleryIntent.Delete.All.Confirm -> launchDeletion {
                deleteAllGalleryUseCase()
            }

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

    private fun observePreferences() {
        launch(dispatchersProvider.immediate) {
            preferenceManager
                .observe()
                .catch { onError(it) }
                .collect { settings ->
                    updateState { it.copy(grid = settings.galleryGrid) }
                }
        }
    }

    private fun observeBackgroundWork() {
        launch(dispatchersProvider.immediate) {
            backgroundWorkObserver
                .observeResult()
                .filterIsInstance<BackgroundWorkResult.Success>()
                .catch { onError(it) }
                .collect { loadMediaStoreInfo() }
        }
    }

    private fun observeGallery() {
        launch(dispatchersProvider.io) {
            requestedLimit
                .flatMapLatest { limit ->
                    combine(
                        getGenerationResultPagedUseCase.observe(
                            limit = limit,
                            offset = GALLERY_FIRST_PAGE,
                        ),
                        getGenerationResultPagedUseCase.observeCount(),
                    ) { results, count ->
                        val items = results.map { result ->
                            GalleryGridItemUi(
                                id = result.id,
                                image = result.image.decodeBase64ImageBitmap(),
                                hidden = result.hidden,
                            )
                        }
                        GalleryPageSnapshot(
                            limit = limit,
                            items = items,
                            totalCount = count,
                        )
                    }
                }
                .catch { t ->
                    onError(t)
                    updateState {
                        it.copy(
                            loading = false,
                            loadingNextPage = false,
                            canLoadMore = false,
                            dialog = GalleryDialog.Error(
                                t.message ?: Localization.string("error_generic"),
                            ),
                        )
                    }
                }
                .collect { snapshot ->
                    val itemIds = snapshot.items.mapTo(mutableSetOf()) { it.id }
                    updateState { current ->
                        current.copy(
                            loading = false,
                            loadingNextPage = false,
                            items = snapshot.items,
                            nextPage = snapshot.limit / GALLERY_PAGE_SIZE,
                            canLoadMore = snapshot.items.size < snapshot.totalCount,
                            selection = current.selection.filter(itemIds::contains),
                        )
                    }
                }
        }
    }

    private fun loadMediaStoreInfo() {
        launch(dispatchersProvider.io) {
            runCatching { getMediaStoreInfoUseCase() }
                .onFailure(onError)
                .onSuccess { info ->
                    updateState { it.copy(mediaStoreInfo = info) }
                }
        }
    }

    private fun loadNextPage() {
        val state = currentState
        if (!state.canLoadMore || state.loading || state.loadingNextPage) return
        updateState {
            it.copy(loadingNextPage = true)
        }
        requestedLimit.value += GALLERY_PAGE_SIZE
    }

    private fun launchDeletion(action: suspend () -> Unit) {
        setActiveDialog(GalleryDialog.None)
        launch(dispatchersProvider.io) {
            runCatching { action() }
                .onFailure { t ->
                    onError(t)
                    setActiveDialog(
                        GalleryDialog.Error(t.message ?: Localization.string("error_generic")),
                    )
                }
                .onSuccess {
                    updateState {
                        it.copy(
                            selectionMode = false,
                            selection = emptyList(),
                        )
                    }
                    refreshGallery()
                }
        }
    }

    private fun launchGalleryExport(exportAll: Boolean) {
        setActiveDialog(GalleryDialog.ExportInProgress)
        launch(dispatchersProvider.io) {
            runCatching {
                galleryExportService.export(if (exportAll) null else currentState.selection)
            }
                .onFailure { t ->
                    onError(t)
                    setActiveDialog(
                        GalleryDialog.Error(t.message ?: Localization.string("error_generic")),
                    )
                }
                .onSuccess { result ->
                    setActiveDialog(GalleryDialog.None)
                    emitEffect(GalleryEffect.ShareExport(result.filePath))
                }
        }
    }

    private fun refreshGallery() {
        loadMediaStoreInfo()
    }

    private fun setActiveDialog(dialog: GalleryDialog) = updateState {
        it.copy(dialog = dialog, dropdownMenuShow = false)
    }
}

private data class GalleryPageSnapshot(
    val limit: Int,
    val items: List<GalleryGridItemUi>,
    val totalCount: Int,
)
