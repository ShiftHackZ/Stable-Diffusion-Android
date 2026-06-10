package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.model.PromptTagEditRequest
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter

internal class ImageToImageIntentProcessor(
    private val router: ImageToImageRouter,
    private val updateState: (((ImageToImageState) -> ImageToImageState) -> Unit),
    private val pickImage: (ImageToImagePickSource) -> Unit,
    private val pickRandomImage: () -> Unit,
    private val generate: () -> Unit,
    private val cancelGeneration: () -> Unit,
    private val saveImage: (String) -> Unit,
    private val shareImage: (String) -> Unit,
    private val saveGenerationResults: (List<AiGenerationResult>) -> Unit,
    private val viewGenerationResult: (AiGenerationResult) -> Unit,
    private val reportGenerationResult: (AiGenerationResult) -> Unit,
    private val applyGenerationResult: (AiGenerationResult) -> Unit,
) {

    fun process(intent: ImageToImageIntent) {
        when (intent) {
            ImageToImageIntent.OpenDrawer -> router.openDrawer()
            ImageToImageIntent.NavigateBack -> router.navigateBack()
            ImageToImageIntent.ConfigureProvider -> router.navigateToServerSetup()
            ImageToImageIntent.NavigateToInPaint -> router.navigateToImageInPaint()
            ImageToImageIntent.PickCamera -> pickImage(ImageToImagePickSource.Camera)
            ImageToImageIntent.PickGallery -> pickImage(ImageToImagePickSource.Gallery)
            ImageToImageIntent.PickRandom -> pickRandomImage()
            ImageToImageIntent.ClearImageInput -> updateState {
                it.copy(
                    imageBase64 = "",
                    inPaint = ImageInPaintState(),
                    results = emptyList(),
                    screenModal = GenerationModal.None,
                    error = null,
                    message = null,
                )
            }
            ImageToImageIntent.Generate -> generate()
            ImageToImageIntent.DismissModal -> updateState { it.copy(screenModal = GenerationModal.None) }
            ImageToImageIntent.CancelGeneration -> cancelGeneration()
            ImageToImageIntent.UndoInPaintStroke -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(
                        strokes = state.inPaint.strokes.dropLast(1),
                    ),
                    message = null,
                )
            }
            ImageToImageIntent.ClearInPaintMask -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(strokes = emptyList()),
                    message = null,
                )
            }
            ImageToImageIntent.DismissError -> updateState { it.copy(error = null) }
            ImageToImageIntent.DismissMessage -> updateState { it.copy(message = null) }
            ImageToImageIntent.DismissEditTag -> updateState { it.copy(editTag = null) }
            is ImageToImageIntent.DrawInPaintStroke -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(
                        strokes = state.inPaint.strokes + intent.stroke,
                    ),
                    error = null,
                    message = null,
                )
            }
            is ImageToImageIntent.SaveResult -> saveImage(intent.base64)
            is ImageToImageIntent.ShareResult -> shareImage(intent.base64)
            is ImageToImageIntent.SaveGenerationResults -> saveGenerationResults(intent.results)
            is ImageToImageIntent.ViewGenerationResult -> viewGenerationResult(intent.result)
            is ImageToImageIntent.ReportGenerationResult -> reportGenerationResult(intent.result)
            is ImageToImageIntent.ShowEditTag -> updateState {
                it.copy(
                    editTag = PromptTagEditRequest(
                        prompt = intent.prompt,
                        negativePrompt = intent.negativePrompt,
                        tag = intent.tag,
                        isNegative = intent.isNegative,
                    ),
                )
            }
            is ImageToImageIntent.ApplyPrompts -> updateState {
                it.copy(
                    prompt = intent.prompt,
                    negativePrompt = intent.negativePrompt,
                    editTag = null,
                    message = null,
                )
            }
            is ImageToImageIntent.ApplyGenerationResult -> applyGenerationResult(intent.ai)
            is ImageToImageIntent.UpdateAdvancedOptionsVisibility -> updateState {
                it.copy(advancedOptionsVisible = intent.visible)
            }
            is ImageToImageIntent.UpdatePrompt -> updateState {
                it.copy(prompt = intent.value, promptValidationError = null, error = null, message = null)
            }
            is ImageToImageIntent.UpdateNegativePrompt -> updateState {
                it.copy(negativePrompt = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateWidth -> updateState {
                it.copy(
                    width = intent.value.filter(Char::isDigit),
                    widthValidationError = null,
                    error = null,
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateHeight -> updateState {
                it.copy(
                    height = intent.value.filter(Char::isDigit),
                    heightValidationError = null,
                    error = null,
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateSamplingSteps -> updateState {
                it.copy(samplingSteps = intent.value.coerceIn(MIN_STEPS, MAX_STEPS), message = null)
            }
            is ImageToImageIntent.UpdateCfgScale -> updateState {
                it.copy(cfgScale = intent.value.coerceIn(MIN_CFG_SCALE, MAX_CFG_SCALE), message = null)
            }
            is ImageToImageIntent.UpdateRestoreFaces -> updateState {
                it.copy(restoreFaces = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateSeed -> updateState {
                it.copy(seed = intent.value.filter(Char::isDigit), message = null)
            }
            is ImageToImageIntent.UpdateSubSeed -> updateState {
                it.copy(subSeed = intent.value.filter(Char::isDigit), message = null)
            }
            is ImageToImageIntent.UpdateSubSeedStrength -> updateState {
                it.copy(
                    subSeedStrength = intent.value.coerceIn(MIN_SUB_SEED_STRENGTH, MAX_SUB_SEED_STRENGTH),
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateSampler -> updateState {
                it.copy(selectedSampler = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateNsfw -> updateState {
                it.copy(nsfw = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateBatchCount -> updateState {
                it.copy(batchCount = intent.value.coerceIn(MIN_BATCH_COUNT, MAX_BATCH_COUNT), message = null)
            }
            is ImageToImageIntent.UpdateOpenAiModel -> updateState { state ->
                val size = if (state.openAiSize.supportedModels.contains(intent.value)) {
                    state.openAiSize
                } else {
                    OpenAiSize.entries.first { it.supportedModels.contains(intent.value) }
                }
                state.copy(openAiModel = intent.value, openAiSize = size, message = null)
            }
            is ImageToImageIntent.UpdateOpenAiSize -> updateState {
                it.copy(openAiSize = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateOpenAiQuality -> updateState {
                it.copy(openAiQuality = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateStabilityAiStyle -> updateState {
                it.copy(selectedStylePreset = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateStabilityAiClipGuidance -> updateState {
                it.copy(selectedClipGuidancePreset = intent.value, message = null)
            }
            is ImageToImageIntent.UpdateDenoisingStrength -> updateState {
                it.copy(
                    denoisingStrength = intent.value.coerceIn(MIN_DENOISING_STRENGTH, MAX_DENOISING_STRENGTH),
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateInPaintBrushSize -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(
                        brushSize = intent.value.coerceIn(
                            IN_PAINT_BRUSH_SIZE_MIN,
                            IN_PAINT_BRUSH_SIZE_MAX,
                        ),
                    ),
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateInPaintMaskBlur -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(
                        maskBlur = intent.value.coerceIn(
                            IN_PAINT_MASK_BLUR_MIN,
                            IN_PAINT_MASK_BLUR_MAX,
                        ),
                    ),
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateInPaintOnlyMaskedPadding -> updateState { state ->
                state.copy(
                    inPaint = state.inPaint.copy(
                        onlyMaskedPaddingPx = intent.value.coerceIn(
                            IN_PAINT_MASK_PADDING_MIN,
                            IN_PAINT_MASK_PADDING_MAX,
                        ),
                    ),
                    message = null,
                )
            }
            is ImageToImageIntent.UpdateInPaintMaskMode -> updateState { state ->
                state.copy(inPaint = state.inPaint.copy(maskMode = intent.value), message = null)
            }
            is ImageToImageIntent.UpdateInPaintMaskContent -> updateState { state ->
                state.copy(inPaint = state.inPaint.copy(maskContent = intent.value), message = null)
            }
            is ImageToImageIntent.UpdateInPaintArea -> updateState { state ->
                state.copy(inPaint = state.inPaint.copy(area = intent.value), message = null)
            }
        }
    }
}
