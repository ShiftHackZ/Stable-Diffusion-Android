package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.allowedModes

/**
 * Exposes the `HUGGING_FACE_MODELS_TIMEOUT_MILLIS` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal const val HUGGING_FACE_MODELS_TIMEOUT_MILLIS = 5_000L

/**
 * Executes the `setupAllowedModes` step in the SDAI presentation layer.
 *
 * @return Result produced by `setupAllowedModes`.
 * @author Dmitriy Moroz
 */
internal fun BuildInfoProvider.setupAllowedModes(): List<ServerSource> =
    allowedModes.filter(::isServerSourceAvailableOnPlatform)

/**
 * Executes the `url` step in the SDAI presentation layer.
 *
 * @param linksProvider links provider value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
internal fun ValidationResult<CommonStringValidator.Error>.mapStringToValidationError():
    ServerSetupState.ValidationError? {
    if (isValid) return null
    return when (validationError) {
        CommonStringValidator.Error.Empty -> ServerSetupState.ValidationError.EmptyField
        null -> null
    }
}

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `function` step in the SDAI presentation layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
internal fun ValidationResult<FilePathValidator.Error>.mapFilePathToValidationError():
    ServerSetupState.ValidationError? {
    if (isValid) return null
    return when (validationError) {
        FilePathValidator.Error.Empty -> ServerSetupState.ValidationError.EmptyField
        FilePathValidator.Error.Invalid -> ServerSetupState.ValidationError.InvalidUrl
        null -> null
    }
}
