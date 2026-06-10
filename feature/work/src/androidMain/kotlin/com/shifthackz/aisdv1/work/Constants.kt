package com.shifthackz.aisdv1.work

/**
 * Provides the `Constants` singleton used by the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
internal object Constants {

    /**
     * Exposes the `NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND = 5598
    /**
     * Exposes the `NOTIFICATION_TEXT_TO_IMAGE_GENERIC` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val NOTIFICATION_TEXT_TO_IMAGE_GENERIC = 5599
    /**
     * Exposes the `NOTIFICATION_IMAGE_TO_IMAGE_FOREGROUND` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val NOTIFICATION_IMAGE_TO_IMAGE_FOREGROUND = 151297
    /**
     * Exposes the `NOTIFICATION_IMAGE_TO_IMAGE_GENERIC` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val NOTIFICATION_IMAGE_TO_IMAGE_GENERIC = 151298

    /**
     * Exposes the `TAG_GENERATION` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val TAG_GENERATION = "work_ai_generation"

    /**
     * Exposes the `FILE_TEXT_TO_IMAGE` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val FILE_TEXT_TO_IMAGE = "txt2img.bin"
    /**
     * Exposes the `FILE_IMAGE_TO_IMAGE` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val FILE_IMAGE_TO_IMAGE = "img2img.bin"
}
