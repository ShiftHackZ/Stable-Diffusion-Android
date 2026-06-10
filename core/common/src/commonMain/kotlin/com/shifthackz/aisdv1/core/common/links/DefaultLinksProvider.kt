package com.shifthackz.aisdv1.core.common.links

object DefaultLinksProvider : LinksProvider {
    override val hordeUrl: String = "https://stablehorde.net/"
    override val hordeSignUpUrl: String = "https://stablehorde.net/register"
    override val huggingFaceUrl: String = "https://huggingface.co/docs/inference-providers/providers/hf-inference"
    override val openAiInfoUrl: String = "https://platform.openai.com/api-keys"
    override val stabilityAiInfoUrl: String = "https://platform.stability.ai/"
    override val privacyPolicyUrl: String = "https://sdai.moroz.cc/policy.html"
    override val donateUrl: String = "https://sdai.moroz.cc/donate.html"
    override val projectWebsiteUrl: String = "https://sdai.moroz.cc"
    override val developerWebsiteUrl: String = "https://moroz.cc"
    override val gitHubSourceUrl: String = "https://github.com/ShiftHackZ/Stable-Diffusion-Android"
    override val licenseUrl: String = "https://github.com/ShiftHackZ/Stable-Diffusion-Android/blob/master/LICENSE"
    override val setupInstructionsUrl: String =
        "https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki"
    override val swarmUiInfoUrl: String = "https://github.com/mcmonkeyprojects/SwarmUI/tree/master/docs"
    override val demoModeUrl: String = "https://sdai.moroz.cc"
    override val telegramCommunityUrl: String = "https://t.me/sdai_app"
    override val discordCommunityUrl: String = "https://discord.gg/jzdR9m8Ves"
}
