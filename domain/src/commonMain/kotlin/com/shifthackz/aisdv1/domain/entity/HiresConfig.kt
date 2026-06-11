package com.shifthackz.aisdv1.domain.entity

/**
 * Carries A1111 Hires.Fix configuration through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class HiresConfig(
    /**
     * Exposes the `enabled` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val enabled: Boolean = false,
    /**
     * Exposes the `upscaler` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val upscaler: String = "None",
    /**
     * Exposes the `scale` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val scale: Float = 2f,
    /**
     * Exposes the `steps` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val steps: Int = 0,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float = 0.4f,
    /**
     * Exposes the `hrCfg` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hrCfg: Float? = null,
    /**
     * Exposes the `hrDistilledCfg` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hrDistilledCfg: Float? = null,
) {
    companion object {
        /**
         * Exposes the `DISABLED` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val DISABLED = HiresConfig(enabled = false)
        /**
         * Exposes the `AVAILABLE_UPSCALERS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val AVAILABLE_UPSCALERS = listOf(
            "Latent",
            "Latent (antialiased)",
            "Latent (bicubic)",
            "Latent (bicubic antialiased)",
            "Latent (nearest)",
            "Latent (nearest-exact)",
            "None",
            "Lanczos",
            "Nearest",
            "ESRGAN_4x",
            "LDSR",
            "R-ESRGAN 4x+",
            "R-ESRGAN 4x+ Anime6B",
            "ScuNET GAN",
            "ScuNET PSNR",
            "SwinIR 4x",
        )
    }
}
