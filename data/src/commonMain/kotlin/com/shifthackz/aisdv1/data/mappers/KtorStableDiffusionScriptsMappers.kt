package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionScripts
import com.shifthackz.aisdv1.network.model.StableDiffusionExtensionRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionScriptInfoRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionScriptsRaw

/**
 * Converts SDAI data with `mapToDomain`.
 *
 * @return Result produced by `mapToDomain`.
 * @author Dmitriy Moroz
 */
internal fun StableDiffusionScriptsRaw.mapToDomain(): StableDiffusionScripts =
    StableDiffusionScripts(
        txt2img = txt2img,
        img2img = img2img,
    )

/**
 * Converts SDAI data with `mapToDomain`.
 *
 * @return Result produced by `mapToDomain`.
 * @author Dmitriy Moroz
 */
internal fun List<StableDiffusionScriptInfoRaw>.mapToDomain(): StableDiffusionScripts =
    StableDiffusionScripts(
        txt2img = filter { !it.isImg2Img }.map(StableDiffusionScriptInfoRaw::name),
        img2img = filter(StableDiffusionScriptInfoRaw::isImg2Img)
            .map(StableDiffusionScriptInfoRaw::name),
    )

/**
 * Converts SDAI data with `mapExtensionsToDomain`.
 *
 * @return Result produced by `mapExtensionsToDomain`.
 * @author Dmitriy Moroz
 */
internal fun List<StableDiffusionExtensionRaw>.mapExtensionsToDomain(): StableDiffusionScripts =
    StableDiffusionScripts(
        extensions = filter(StableDiffusionExtensionRaw::enabled)
            .map(StableDiffusionExtensionRaw::name),
    )

/**
 * Converts SDAI data with `merge`.
 *
 * @param other other value consumed by the API.
 * @return Result produced by `merge`.
 * @author Dmitriy Moroz
 */
internal fun StableDiffusionScripts.merge(other: StableDiffusionScripts): StableDiffusionScripts =
    StableDiffusionScripts(
        txt2img = (txt2img + other.txt2img).distinct(),
        img2img = (img2img + other.img2img).distinct(),
        extensions = (extensions + other.extensions).distinct(),
    )
