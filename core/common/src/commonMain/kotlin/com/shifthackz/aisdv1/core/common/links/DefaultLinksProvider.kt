package com.shifthackz.aisdv1.core.common.links

/**
 * Provides the `DefaultLinksProvider` singleton used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
object DefaultLinksProvider : LinksProvider {
    /**
     * Exposes the `hordeUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val hordeUrl: String = "https://stablehorde.net/"
    /**
     * Exposes the `hordeSignUpUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val hordeSignUpUrl: String = "https://stablehorde.net/register"
    /**
     * Exposes the `huggingFaceUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val huggingFaceUrl: String = "https://huggingface.co/docs/inference-providers/providers/hf-inference"
    /**
     * Exposes the `openAiInfoUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val openAiInfoUrl: String = "https://platform.openai.com/api-keys"
    /**
     * Exposes the `stabilityAiInfoUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val stabilityAiInfoUrl: String = "https://platform.stability.ai/"
    /**
     * Exposes the `privacyPolicyUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val privacyPolicyUrl: String = "https://sdai.moroz.cc/policy.html"
    /**
     * Exposes the `donateUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val donateUrl: String = "https://sdai.moroz.cc/donate.html"
    /**
     * Exposes the `projectWebsiteUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val projectWebsiteUrl: String = "https://sdai.moroz.cc"
    /**
     * Exposes the `developerWebsiteUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val developerWebsiteUrl: String = "https://moroz.cc"
    /**
     * Exposes the `gitHubSourceUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val gitHubSourceUrl: String = "https://github.com/ShiftHackZ/Stable-Diffusion-Android"
    /**
     * Exposes the `licenseUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val licenseUrl: String = "https://github.com/ShiftHackZ/Stable-Diffusion-Android/blob/master/LICENSE"
    /**
     * Exposes the `setupInstructionsUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val setupInstructionsUrl: String =
        "https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki"
    /**
     * Exposes the `swarmUiInfoUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val swarmUiInfoUrl: String = "https://github.com/mcmonkeyprojects/SwarmUI/tree/master/docs"
    /**
     * Exposes the `demoModeUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val demoModeUrl: String = "https://sdai.moroz.cc"
    /**
     * Exposes the `telegramCommunityUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val telegramCommunityUrl: String = "https://t.me/sdai_app"
    /**
     * Exposes the `discordCommunityUrl` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override val discordCommunityUrl: String = "https://discord.gg/jzdR9m8Ves"
}
