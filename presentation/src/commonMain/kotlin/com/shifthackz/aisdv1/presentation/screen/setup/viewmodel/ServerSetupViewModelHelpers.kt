package com.shifthackz.aisdv1.presentation.screen.setup.viewmodel

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.allowedModes
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupLink
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.platform.isServerSourceAvailableOnPlatform

internal const val HUGGING_FACE_MODELS_TIMEOUT_MILLIS = 5_000L

internal fun BuildInfoProvider.setupAllowedModes(): List<ServerSource> =
    allowedModes.filter(::isServerSourceAvailableOnPlatform)

internal fun ServerSetupLink.url(linksProvider: LinksProvider): String = when (this) {
    ServerSetupLink.A1111Instructions -> linksProvider.setupInstructionsUrl
    ServerSetupLink.SwarmUiInstructions -> linksProvider.swarmUiInfoUrl
    ServerSetupLink.HordeInfo -> linksProvider.hordeUrl
    ServerSetupLink.HordeSignUp -> linksProvider.hordeSignUpUrl
    ServerSetupLink.HuggingFaceInfo -> linksProvider.huggingFaceUrl
    ServerSetupLink.OpenAiInfo -> linksProvider.openAiInfoUrl
    ServerSetupLink.StabilityAiInfo -> linksProvider.stabilityAiInfoUrl
    ServerSetupLink.FalAiInfo -> linksProvider.falAiInfoUrl
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
