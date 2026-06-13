package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Carries `ServerSetupValidationResult` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal data class ServerSetupValidationResult(
    /**
     * Exposes the `isValid` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isValid: Boolean,
    /**
     * Exposes the `state` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val state: ServerSetupState,
)

/**
 * Executes the `validateServerSetup` step in the SDAI presentation layer.
 *
 * @param urlValidator url validator value consumed by the API.
 * @param stringValidator string validator value consumed by the API.
 * @param filePathValidator file path validator value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun ServerSetupState.validateServerSetup(
    urlValidator: UrlValidator,
    stringValidator: CommonStringValidator,
    filePathValidator: FilePathValidator,
): ServerSetupValidationResult = when (mode) {
    ServerSource.AUTOMATIC1111 -> {
        if (demoMode) ServerSetupValidationResult(true, this)
        else validateServerUrlAndCredentials(serverUrl, urlValidator, stringValidator)
    }

    ServerSource.SWARM_UI -> validateServerUrlAndCredentials(swarmUiUrl, urlValidator, stringValidator)
    ServerSource.HORDE -> validateApiKey(
        key = hordeApiKey,
        stringValidator = stringValidator,
        useDefault = hordeDefaultApiKey,
        update = { error -> copy(hordeApiKeyValidationError = error) },
    )

    ServerSource.HUGGING_FACE -> validateApiKey(
        key = huggingFaceApiKey,
        stringValidator = stringValidator,
        update = { error -> copy(huggingFaceApiKeyValidationError = error) },
    )

    ServerSource.OPEN_AI -> validateApiKey(
        key = openAiApiKey,
        stringValidator = stringValidator,
        update = { error -> copy(openAiApiKeyValidationError = error) },
    )

    ServerSource.STABILITY_AI -> validateApiKey(
        key = stabilityAiApiKey,
        stringValidator = stringValidator,
        update = { error -> copy(stabilityAiApiKeyValidationError = error) },
    )

    ServerSource.FAL_AI -> validateApiKey(
        key = falAiApiKey,
        stringValidator = stringValidator,
        update = { error -> copy(falAiApiKeyValidationError = error) },
    )

    ServerSource.LOCAL_MICROSOFT_ONNX -> validateLocalModel(
        customModel = localOnnxCustomModel,
        customModelPath = localOnnxCustomModelPath,
        hasDownloadedSelection = localOnnxModels.any { it.selected && it.downloaded },
        filePathValidator = filePathValidator,
        update = { error -> copy(localCustomOnnxPathValidationError = error) },
    )

    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> validateLocalModel(
        customModel = localMediaPipeCustomModel,
        customModelPath = localMediaPipeCustomModelPath,
        hasDownloadedSelection = localMediaPipeModels.any { it.selected && it.downloaded },
        filePathValidator = filePathValidator,
        update = { error -> copy(localCustomMediaPipePathValidationError = error) },
    )

    ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> validateLocalModel(
        customModel = localSdxlCustomModel,
        customModelPath = localSdxlCustomModelPath,
        hasDownloadedSelection = localSdxlModels.any { it.selected && it.downloaded },
        filePathValidator = filePathValidator,
        update = { error -> copy(localCustomSdxlPathValidationError = error) },
    )

    ServerSource.LOCAL_APPLE_CORE_ML -> ServerSetupValidationResult(
        isValid = localCoreMlModels.any { it.selected && it.downloaded },
        state = this,
    )
}

/**
 * Executes the `validateApiKey` step in the SDAI presentation layer.
 *
 * @param key key value consumed by the API.
 * @param stringValidator string validator value consumed by the API.
 * @param useDefault use default value consumed by the API.
 * @param update update value consumed by the API.
 * @return Result produced by `validateApiKey`.
 * @author Dmitriy Moroz
 */
private fun ServerSetupState.validateApiKey(
    key: String,
    stringValidator: CommonStringValidator,
    useDefault: Boolean = false,
    update: ServerSetupState.(ServerSetupState.ValidationError?) -> ServerSetupState,
): ServerSetupValidationResult {
    if (useDefault) return ServerSetupValidationResult(true, this)
    val validation = stringValidator(key)
    val error = validation.mapStringToValidationError()
    return ServerSetupValidationResult(validation.isValid, update(error))
}

/**
 * Executes the `validateLocalModel` step in the SDAI presentation layer.
 *
 * @param customModel custom model value consumed by the API.
 * @param customModelPath custom model path value consumed by the API.
 * @param hasDownloadedSelection has downloaded selection value consumed by the API.
 * @param filePathValidator file path validator value consumed by the API.
 * @param update update value consumed by the API.
 * @return Result produced by `validateLocalModel`.
 * @author Dmitriy Moroz
 */
private fun ServerSetupState.validateLocalModel(
    customModel: Boolean,
    customModelPath: String,
    hasDownloadedSelection: Boolean,
    filePathValidator: FilePathValidator,
    update: ServerSetupState.(ServerSetupState.ValidationError?) -> ServerSetupState,
): ServerSetupValidationResult {
    if (!customModel) return ServerSetupValidationResult(hasDownloadedSelection, this)
    val validation = filePathValidator(customModelPath)
    val error = validation.mapFilePathToValidationError()
    return ServerSetupValidationResult(validation.isValid, update(error))
}

/**
 * Executes the `validateServerUrlAndCredentials` step in the SDAI presentation layer.
 *
 * @param url remote URL used by the operation.
 * @param urlValidator url validator value consumed by the API.
 * @param stringValidator string validator value consumed by the API.
 * @return Result produced by `validateServerUrlAndCredentials`.
 * @author Dmitriy Moroz
 */
private fun ServerSetupState.validateServerUrlAndCredentials(
    url: String,
    urlValidator: UrlValidator,
    stringValidator: CommonStringValidator,
): ServerSetupValidationResult {
    val urlValidation = urlValidator(url)
    var isValid = urlValidation.isValid
    val urlError = urlValidation.mapUrlToValidationError()
    var newState = copy(
        serverUrlValidationError = if (mode == ServerSource.AUTOMATIC1111) {
            urlError
        } else {
            serverUrlValidationError
        },
        swarmUiUrlValidationError = if (mode == ServerSource.SWARM_UI) {
            urlError
        } else {
            swarmUiUrlValidationError
        },
    )
    if (authType == ServerSetupState.AuthType.HTTP_BASIC) {
        val loginValidation = stringValidator(login)
        val passwordValidation = stringValidator(password)
        val loginError = loginValidation.mapStringToValidationError()
        val passwordError = passwordValidation.mapStringToValidationError()
        newState = newState.copy(
            loginValidationError = loginError,
            passwordValidationError = passwordError,
        )
        isValid = isValid && loginValidation.isValid && passwordValidation.isValid
    }
    if (urlValidation.validationError is UrlValidator.Error.Localhost
        && newState.loginValidationError == null
        && newState.passwordValidationError == null
    ) {
        newState = newState.copy(modal = ServerSetupState.Modal.ConnectLocalHost)
    }
    return ServerSetupValidationResult(isValid, newState)
}
