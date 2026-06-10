package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.presentation.model.PromptTagEditRequest
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter

internal class TextToImageIntentProcessor(
    private val router: TextToImageRouter,
    private val updateState: (((TextToImageState) -> TextToImageState) -> Unit),
    private val generate: () -> Unit,
    private val cancelGeneration: () -> Unit,
    private val saveImage: (String) -> Unit,
    private val shareImage: (String) -> Unit,
    private val saveGenerationResults: (List<AiGenerationResult>) -> Unit,
    private val viewGenerationResult: (AiGenerationResult) -> Unit,
    private val reportGenerationResult: (AiGenerationResult) -> Unit,
    private val applyGenerationResult: (AiGenerationResult) -> Unit,
) {

    fun process(intent: TextToImageIntent) {
        when (intent) {
            TextToImageIntent.OpenDrawer -> router.openDrawer()
            TextToImageIntent.NavigateBack -> router.navigateBack()
            TextToImageIntent.ConfigureProvider -> router.navigateToServerSetup()
            TextToImageIntent.Generate -> generate()
            TextToImageIntent.DismissModal -> updateState { it.copy(screenModal = com.shifthackz.aisdv1.presentation.model.GenerationModal.None) }
            TextToImageIntent.CancelGeneration -> cancelGeneration()
            TextToImageIntent.DismissError -> updateState { it.copy(error = null) }
            TextToImageIntent.DismissMessage -> updateState { it.copy(message = null) }
            TextToImageIntent.DismissEditTag -> updateState { it.copy(editTag = null) }
            is TextToImageIntent.SaveResult -> saveImage(intent.base64)
            is TextToImageIntent.ShareResult -> shareImage(intent.base64)
            is TextToImageIntent.SaveGenerationResults -> saveGenerationResults(intent.results)
            is TextToImageIntent.ViewGenerationResult -> viewGenerationResult(intent.result)
            is TextToImageIntent.ReportGenerationResult -> reportGenerationResult(intent.result)
            is TextToImageIntent.ShowEditTag -> updateState {
                it.copy(
                    editTag = PromptTagEditRequest(
                        prompt = intent.prompt,
                        negativePrompt = intent.negativePrompt,
                        tag = intent.tag,
                        isNegative = intent.isNegative,
                    ),
                )
            }
            is TextToImageIntent.ApplyPrompts -> updateState {
                it.copy(
                    prompt = intent.prompt,
                    negativePrompt = intent.negativePrompt,
                    editTag = null,
                    message = null,
                )
            }
            is TextToImageIntent.ApplyGenerationResult -> applyGenerationResult(intent.ai)
            is TextToImageIntent.UpdateAdvancedOptionsVisibility -> updateState {
                it.copy(advancedOptionsVisible = intent.visible)
            }
            is TextToImageIntent.UpdatePrompt -> updateState {
                it.copy(prompt = intent.value, promptValidationError = null, error = null, message = null)
            }
            is TextToImageIntent.UpdateNegativePrompt -> updateState {
                it.copy(negativePrompt = intent.value, message = null)
            }
            is TextToImageIntent.UpdateWidth -> updateState {
                it.copy(
                    width = intent.value.filter(Char::isDigit),
                    widthValidationError = null,
                    error = null,
                    message = null,
                )
            }
            is TextToImageIntent.UpdateHeight -> updateState {
                it.copy(
                    height = intent.value.filter(Char::isDigit),
                    heightValidationError = null,
                    error = null,
                    message = null,
                )
            }
            is TextToImageIntent.UpdateSamplingSteps -> updateState {
                it.copy(samplingSteps = intent.value.coerceIn(MIN_STEPS, MAX_STEPS), message = null)
            }
            is TextToImageIntent.UpdateCfgScale -> updateState {
                it.copy(cfgScale = intent.value.coerceIn(MIN_CFG_SCALE, MAX_CFG_SCALE), message = null)
            }
            is TextToImageIntent.UpdateRestoreFaces -> updateState {
                it.copy(restoreFaces = intent.value, message = null)
            }
            is TextToImageIntent.UpdateSeed -> updateState {
                it.copy(seed = intent.value.filter(Char::isDigit), message = null)
            }
            is TextToImageIntent.UpdateSubSeed -> updateState {
                it.copy(subSeed = intent.value.filter(Char::isDigit), message = null)
            }
            is TextToImageIntent.UpdateSubSeedStrength -> updateState {
                it.copy(
                    subSeedStrength = intent.value.coerceIn(MIN_SUB_SEED_STRENGTH, MAX_SUB_SEED_STRENGTH),
                    message = null,
                )
            }
            is TextToImageIntent.UpdateSampler -> updateState {
                it.copy(selectedSampler = intent.value, message = null)
            }
            is TextToImageIntent.UpdateNsfw -> updateState {
                it.copy(nsfw = intent.value, message = null)
            }
            is TextToImageIntent.UpdateBatchCount -> updateState {
                it.copy(batchCount = intent.value.coerceIn(MIN_BATCH_COUNT, MAX_BATCH_COUNT), message = null)
            }
            is TextToImageIntent.UpdateOpenAiModel -> updateState { state ->
                val size = if (state.openAiSize.supportedModels.contains(intent.value)) {
                    state.openAiSize
                } else {
                    OpenAiSize
                        .entries
                        .first { it.supportedModels.contains(intent.value) }
                }
                state.copy(openAiModel = intent.value, openAiSize = size, message = null)
            }
            is TextToImageIntent.UpdateOpenAiSize -> updateState {
                it.copy(openAiSize = intent.value, message = null)
            }
            is TextToImageIntent.UpdateOpenAiQuality -> updateState {
                it.copy(openAiQuality = intent.value, message = null)
            }
            is TextToImageIntent.UpdateStabilityAiStyle -> updateState {
                it.copy(selectedStylePreset = intent.value, message = null)
            }
            is TextToImageIntent.UpdateStabilityAiClipGuidance -> updateState {
                it.copy(selectedClipGuidancePreset = intent.value, message = null)
            }
        }
    }
}
