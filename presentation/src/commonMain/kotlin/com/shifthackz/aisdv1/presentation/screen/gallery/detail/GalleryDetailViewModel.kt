package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.DeleteGalleryItemUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageLikeUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageVisibilityUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Coordinates `GalleryDetailViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class GalleryDetailViewModel(
    /**
     * Exposes the `itemId` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val itemId: Long,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `getGenerationResultUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    /**
     * Exposes the `getAllGalleryUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    /**
     * Exposes the `getLastResultFromCacheUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    /**
     * Exposes the `deleteGalleryItemUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val deleteGalleryItemUseCase: DeleteGalleryItemUseCase,
    /**
     * Exposes the `toggleImageVisibilityUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val toggleImageVisibilityUseCase: ToggleImageVisibilityUseCase,
    /**
     * Exposes the `toggleImageLikeUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val toggleImageLikeUseCase: ToggleImageLikeUseCase,
    /**
     * Exposes the `generationFormUpdateEvent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: GalleryDetailRouter,
    /**
     * Exposes the `platformActions` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val platformActions: GalleryDetailPlatformActions,
    /**
     * Exposes the `pagerBuffer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val pagerBuffer: Int = GALLERY_DETAIL_PAGER_BUFFER,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<GalleryDetailState, GalleryDetailIntent, EmptyEffect>(
    initialState = GalleryDetailState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        load()
    }

    private var currentItemId: Long = itemId
    private val safePagerBuffer = pagerBuffer.coerceAtLeast(0)

    override fun processIntent(intent: GalleryDetailIntent) {
        when (intent) {
            is GalleryDetailIntent.CopyToClipboard -> copyToClipboard(intent.content)
            GalleryDetailIntent.Delete.Request -> setActiveDialog(GalleryDetailDialog.DeleteConfirm)
            GalleryDetailIntent.Delete.Confirm -> delete()
            GalleryDetailIntent.DismissDialog -> setActiveDialog(GalleryDetailDialog.None)
            GalleryDetailIntent.Export.Image -> saveImage()
            GalleryDetailIntent.Export.Params -> shareParams()
            GalleryDetailIntent.NavigateBack -> router.navigateBack()
            GalleryDetailIntent.NavigatePrevious -> navigateToAdjacent(delta = -1)
            GalleryDetailIntent.NavigateNext -> navigateToAdjacent(delta = 1)
            is GalleryDetailIntent.NavigateToPage -> navigateToPage(intent.page)
            GalleryDetailIntent.Report -> currentState.content?.id?.let(router::navigateToReportImage)
            is GalleryDetailIntent.SelectTab -> updateState { it.copy(selectedTab = intent.tab) }
            GalleryDetailIntent.SendTo.Img2Img -> sendPromptToGenerationScreen(
                AiGenerationResult.Type.IMAGE_TO_IMAGE,
            )
            GalleryDetailIntent.SendTo.Txt2Img -> sendPromptToGenerationScreen(
                AiGenerationResult.Type.TEXT_TO_IMAGE,
            )
            GalleryDetailIntent.Share.Image -> shareImage()
            GalleryDetailIntent.Share.Params -> shareParams()
            GalleryDetailIntent.ToggleLike -> toggleLike()
            GalleryDetailIntent.ToggleVisibility -> toggleVisibility()
        }
    }

    private fun load() {
        launch(dispatchersProvider.io) {
            runCatching {
                val galleryItems = getGalleryItems()
                val result = galleryItems.firstOrNull { it.id == currentItemId }
                    ?: getGenerationResult(currentItemId)
                Triple(result, galleryItems, galleryItems.map(AiGenerationResult::id))
            }
                .onFailure(::handleFailure)
                .onSuccess { (result, galleryItems, galleryItemIds) ->
                    val tabs = GalleryDetailTab.consume(result.type)
                    val selectedTab = currentState.selectedTab.takeIf(tabs::contains) ?: tabs.first()
                    val itemIndex = galleryItems.indexOfFirst { it.id == result.id }
                    val content = result.toGalleryDetailContent(
                        showReportButton = buildInfoProvider.type != BuildType.FOSS,
                    )
                    val pagerWindow = createPagerWindow(
                        galleryItems = galleryItems,
                        itemIndex = itemIndex,
                        content = content,
                        showReportButton = buildInfoProvider.type != BuildType.FOSS,
                    )
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                tabs = tabs,
                                selectedTab = selectedTab,
                                galleryItemIds = galleryItemIds,
                                content = content,
                                pagerContents = pagerWindow.contents,
                                pagerContentStartIndex = pagerWindow.startIndex,
                                pagerCurrentIndex = pagerWindow.currentIndex,
                            )
                        }
                    }
                }
        }
    }

    private fun copyToClipboard(text: String) {
        launch(dispatchersProvider.io) {
            platformActions.copyText(text).handlePlatformResult()
        }
    }

    private fun saveImage() {
        val content = currentState.content ?: return
        val imageBase64 = content.selectedImageBase64(currentState.selectedTab)
        launch(dispatchersProvider.io) {
            platformActions.saveImage(imageBase64).handlePlatformResult()
        }
    }

    private fun shareParams() {
        val text = currentState.content?.paramsText()?.takeIf(String::isNotEmpty) ?: return
        launch(dispatchersProvider.io) {
            platformActions.shareText(text).handlePlatformResult()
        }
    }

    private fun shareImage() {
        val content = currentState.content ?: return
        val imageBase64 = content.selectedImageBase64(currentState.selectedTab)
        launch(dispatchersProvider.io) {
            platformActions.shareImage(imageBase64).handlePlatformResult()
        }
    }

    private fun delete() {
        setActiveDialog(GalleryDetailDialog.None)
        val id = currentState.content?.id ?: return
        launch(dispatchersProvider.io) {
            runCatching { deleteGalleryItemUseCase(id) }
                .onFailure(::handleFailure)
                .onSuccess {
                    withContext(dispatchersProvider.immediate) {
                        router.navigateBack()
                    }
                }
        }
    }

    private fun sendPromptToGenerationScreen(screenType: AiGenerationResult.Type) {
        val selectedTab = currentState.selectedTab
        val id = currentState.content?.id ?: currentItemId
        launch(dispatchersProvider.io) {
            runCatching { getGenerationResult(id) }
                .onFailure(::handleFailure)
                .onSuccess { result ->
                    generationFormUpdateEvent.update(
                        generation = result,
                        route = screenType,
                        inputImage = selectedTab == GalleryDetailTab.ORIGINAL,
                    )
                    withContext(dispatchersProvider.immediate) {
                        when (screenType) {
                            AiGenerationResult.Type.TEXT_TO_IMAGE -> router.navigateToTextToImage()
                            AiGenerationResult.Type.IMAGE_TO_IMAGE -> router.navigateToImageToImage()
                        }
                    }
                }
        }
    }

    private fun toggleVisibility() {
        val id = currentState.content?.id ?: return
        launch(dispatchersProvider.io) {
            runCatching { toggleImageVisibilityUseCase(id) }
                .onFailure(::handleFailure)
                .onSuccess { hidden ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { state ->
                            state.copy(
                                content = state.content?.copy(hidden = hidden),
                                pagerContents = state.pagerContents.updateContent(
                                    id = id,
                                    update = { content -> content.copy(hidden = hidden) },
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun toggleLike() {
        val id = currentState.content?.id ?: return
        launch(dispatchersProvider.io) {
            runCatching { toggleImageLikeUseCase(id) }
                .onFailure(::handleFailure)
                .onSuccess { liked ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { state ->
                            state.copy(
                                content = state.content?.copy(liked = liked),
                                pagerContents = state.pagerContents.updateContent(
                                    id = id,
                                    update = { content -> content.copy(liked = liked) },
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun setActiveDialog(dialog: GalleryDetailDialog) {
        updateState { it.copy(dialog = dialog) }
    }

    private fun navigateToAdjacent(delta: Int) {
        navigateToPage(currentState.pagerCurrentIndex + delta)
    }

    private fun navigateToPage(page: Int) {
        val targetId = currentState.galleryItemIds.getOrNull(page) ?: return
        if (targetId == currentState.content?.id) return
        currentItemId = targetId
        load()
    }

    private suspend fun GalleryDetailActionResult.handlePlatformResult() {
        when (this) {
            GalleryDetailActionResult.Done -> Unit
            GalleryDetailActionResult.Unsupported -> withContext(dispatchersProvider.immediate) {
                setActiveDialog(
                    GalleryDetailDialog.Error(Localization.string("error_generic")),
                )
            }
            is GalleryDetailActionResult.Failed -> withContext(dispatchersProvider.immediate) {
                setActiveDialog(GalleryDetailDialog.Error(message))
            }
        }
    }

    private fun handleFailure(t: Throwable) {
        if (t is CancellationException) throw t
        onError(t)
        updateState {
            it.copy(
                loading = false,
                dialog = GalleryDetailDialog.Error(
                    t.message ?: Localization.string("error_generic"),
                ),
            )
        }
    }

    private suspend fun getGenerationResult(id: Long): AiGenerationResult {
        if (id <= 0) return getLastResultFromCacheUseCase()
        return getGenerationResultUseCase(id)
    }

    private suspend fun getGalleryItems(): List<AiGenerationResult> =
        if (currentItemId <= 0) emptyList() else getAllGalleryUseCase()

    private fun createPagerWindow(
        galleryItems: List<AiGenerationResult>,
        itemIndex: Int,
        content: GalleryDetailContent,
        showReportButton: Boolean,
    ): GalleryDetailPagerWindow {
        if (itemIndex == -1 || galleryItems.isEmpty()) {
            return GalleryDetailPagerWindow(
                contents = listOf(content),
                startIndex = 0,
                currentIndex = 0,
            )
        }

        val startIndex = (itemIndex - safePagerBuffer).coerceAtLeast(0)
        val endIndex = (itemIndex + safePagerBuffer + 1).coerceAtMost(galleryItems.size)
        return GalleryDetailPagerWindow(
            contents = galleryItems
                .subList(startIndex, endIndex)
                .map { item ->
                    item.toGalleryDetailContent(showReportButton = showReportButton)
                },
            startIndex = startIndex,
            currentIndex = itemIndex,
        )
    }

    private fun List<GalleryDetailContent>.updateContent(
        id: Long,
        update: (GalleryDetailContent) -> GalleryDetailContent,
    ): List<GalleryDetailContent> = map { content ->
        if (content.id == id) update(content) else content
    }

    private data class GalleryDetailPagerWindow(
        val contents: List<GalleryDetailContent>,
        val startIndex: Int,
        val currentIndex: Int,
    )
}

internal const val GALLERY_DETAIL_PAGER_BUFFER = 3
