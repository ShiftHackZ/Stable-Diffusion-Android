import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.generic.application)
    alias(libs.plugins.generic.baseline.profm)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.shifthackz.aisdv1.app"
    defaultConfig {
        applicationId = "com.shifthackz.aisdv1.app"
        versionName = libs.versions.versionName.get()
        versionCode = libs.versions.versionCode.get().toInt()

        buildConfigField("String", "IMAGE_CDN_URL", "\"https://random.imagecdn.app/\"")
        buildConfigField("String", "HUGGING_FACE_URL", "\"https://huggingface.co/\"")
        buildConfigField("String", "HUGGING_FACE_INFERENCE_URL", "\"https://api-inference.huggingface.co/\"")
        buildConfigField("String", "HORDE_AI_URL", "\"https://stablehorde.net/\"")
        buildConfigField("String", "OPEN_AI_URL", "\"https://api.openai.com/\"")
        buildConfigField("String", "STABILITY_AI_URL", "\"https://api.stability.ai/\"")

        buildConfigField("String", "HORDE_AI_SIGN_UP_URL", "\"https://stablehorde.net/register\"")
        buildConfigField("String", "HUGGING_FACE_INFO_URL", "\"https://huggingface.co/docs/api-inference/index\"")
        buildConfigField("String", "OPEN_AI_INFO_URL", "\"https://platform.openai.com/api-keys\"")
        buildConfigField("String", "STABILITY_AI_INFO_URL", "\"https://platform.stability.ai/\"")
        buildConfigField("String", "UPDATE_API_URL", "\"https://sdai.moroz.cc\"")
        buildConfigField("String", "REPORT_API_URL", "\"https://sdai-report.moroz.cc\"")
        buildConfigField("String", "DEMO_MODE_API_URL", "\"https://sdai.moroz.cc\"")
        buildConfigField("String", "POLICY_URL", "\"https://sdai.moroz.cc/policy.html\"")
        buildConfigField("String", "DONATE_URL", "\"https://www.buymeacoffee.com/shifthackz\"")
        buildConfigField("String", "GITHUB_SOURCE_URL", "\"https://github.com/ShiftHackZ/Stable-Diffusion-Android\"")
        buildConfigField("String", "SETUP_INSTRUCTIONS_URL", "\"https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki\"")
        buildConfigField("String", "SWARM_UI_INFO_URL", "\"https://github.com/mcmonkeyprojects/SwarmUI/tree/master/docs\"")

        resourceConfigurations += listOf("en", "ru", "uk", "tr", "zh")
    }

    val hasPropertiesFile = File("app/keystore/signing.properties").exists()
    if (hasPropertiesFile) {
        val props = Properties()
        props.load(FileInputStream(file("keystore/signing.properties")))
        val alias = props["keystore.alias"] as String
        signingConfigs {
            create("release") {
                storeFile = file(props["keystore"] as String)
                storePassword = props["keystore.password"] as String
                keyAlias = alias
                keyPassword = props["keystore.password"] as String
            }
        }
        println("[Signature] -> Build will be signed with signature: $alias")
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("release")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:imageprocessing"))
    implementation(project(":core:notification"))
    implementation(project(":core:validation"))
    implementation(project(":presentation"))
    implementation(project(":network"))
    implementation(project(":storage"))
    implementation(project(":domain"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:diffusion"))
    implementation(project(":feature:mediapipe"))
    implementation(project(":feature:work"))
    implementation(project(":data"))
    implementation(project(":demo"))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.rx.kotlin)
    implementation(libs.rx.android)
    implementation(libs.timber)
    implementation(libs.shifthackz.catppuccin.splash)
    implementation(libs.shifthackz.catppuccin.legacy)
    implementation(libs.androidx.work.runtime)
}
