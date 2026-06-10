package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.allowedModes

internal const val HUGGING_FACE_MODELS_TIMEOUT_MILLIS = 5_000L

private val localGenerationSources = setOf(
    ServerSource.LOCAL_MICROSOFT_ONNX,
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
)

internal fun BuildInfoProvider.setupAllowedModes(): List<ServerSource> =
    allowedModes.filter { source ->
        source !in localGenerationSources || isLocalGenerationSetupAvailable()
    }

internal fun ServerSetupLink.url(linksProvider: LinksProvider): String = when (this) {
    ServerSetupLink.A1111Instructions -> linksProvider.setupInstructionsUrl
    ServerSetupLink.SwarmUiInstructions -> linksProvider.swarmUiInfoUrl
    ServerSetupLink.HordeInfo -> linksProvider.hordeUrl
    ServerSetupLink.HordeSignUp -> linksProvider.hordeSignUpUrl
    ServerSetupLink.HuggingFaceInfo -> linksProvider.huggingFaceUrl
    ServerSetupLink.OpenAiInfo -> linksProvider.openAiInfoUrl
    ServerSetupLink.StabilityAiInfo -> linksProvider.stabilityAiInfoUrl
}

internal fun ValidationResult<CommonStringValidator.Error>.mapStringToValidationError():
    ServerSetupState.ValidationError? {
    if (isValid) return null
    return when (validationError) {
        CommonStringValidator.Error.Empty -> ServerSetupState.ValidationError.EmptyField
        null -> null
    }
}

internal fun ValidationResult<UrlValidator.Error>.mapUrlToValidationError():
    ServerSetupState.ValidationError? {
    if (isValid) return null
    return when (validationError) {
        UrlValidator.Error.Empty -> ServerSetupState.ValidationError.EmptyUrl
        UrlValidator.Error.BadScheme -> ServerSetupState.ValidationError.InvalidScheme
        UrlValidator.Error.BadPort -> ServerSetupState.ValidationError.InvalidPort
        UrlValidator.Error.Invalid -> ServerSetupState.ValidationError.InvalidUrl
        UrlValidator.Error.Localhost,
        null,
        -> null
    }
}

internal fun ValidationResult<FilePathValidator.Error>.mapFilePathToValidationError():
    ServerSetupState.ValidationError? {
    if (isValid) return null
    return when (validationError) {
        FilePathValidator.Error.Empty -> ServerSetupState.ValidationError.EmptyField
        FilePathValidator.Error.Invalid -> ServerSetupState.ValidationError.InvalidUrl
        null -> null
    }
}
