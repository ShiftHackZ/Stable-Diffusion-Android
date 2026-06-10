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
import com.shifthackz.aisdv1.domain.usecase.gallery.ToggleImageVisibilityUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class GalleryDetailViewModel(
    private val itemId: Long,
    private val dispatchersProvider: DispatchersProvider,
    private val buildInfoProvider: BuildInfoProvider,
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    private val deleteGalleryItemUseCase: DeleteGalleryItemUseCase,
    private val toggleImageVisibilityUseCase: ToggleImageVisibilityUseCase,
    private val generationFormUpdateEvent: GenerationFormUpdateEvent,
    private val router: GalleryDetailRouter,
    private val platformActions: GalleryDetailPlatformActions,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<GalleryDetailState, GalleryDetailIntent, EmptyEffect>(
    initialState = GalleryDetailState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        load()
    }

    override fun processIntent(intent: GalleryDetailIntent) {
        when (intent) {
            is GalleryDetailIntent.CopyToClipboard -> copyToClipboard(intent.content)
            GalleryDetailIntent.Delete.Request -> setActiveDialog(GalleryDetailDialog.DeleteConfirm)
            GalleryDetailIntent.Delete.Confirm -> delete()
            GalleryDetailIntent.DismissDialog -> setActiveDialog(GalleryDetailDialog.None)
            GalleryDetailIntent.Export.Image -> saveImage()
            GalleryDetailIntent.Export.Params -> shareParams()
            GalleryDetailIntent.NavigateBack -> router.navigateBack()
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
            GalleryDetailIntent.ToggleVisibility -> toggleVisibility()
        }
    }

    private fun load() {
        launch(dispatchersProvider.io) {
            runCatching { getGenerationResult(itemId) }
                .onFailure(::handleFailure)
                .onSuccess { result ->
                    val tabs = GalleryDetailTab.consume(result.type)
                    val selectedTab = currentState.selectedTab.takeIf(tabs::contains) ?: tabs.first()
                    val content = result.toGalleryDetailContent(
                        showReportButton = buildInfoProvider.type != BuildType.FOSS,
                    )
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                tabs = tabs,
                                selectedTab = selectedTab,
                                content = content,
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
        launch(dispatchersProvider.io) {
            runCatching { getGenerationResult(itemId) }
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
                            )
                        }
                    }
                }
        }
    }

    private fun setActiveDialog(dialog: GalleryDetailDialog) {
        updateState { it.copy(dialog = dialog) }
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
}
