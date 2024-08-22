@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

import com.android.tools.profgen.ArtProfile
import com.android.tools.profgen.ArtProfileSerializer
import com.android.tools.profgen.DexFile
import com.android.tools.profgen.DexFileData
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Collections
import java.util.Properties

plugins {
    alias(libs.plugins.generic.application)
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
                keyAlias = props["keystore.alias"] as String
                keyPassword = props["keystore.password"] as String
            }
        }
        println("[Signature] -> Build will be signed with signature: $alias")
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("release")
    }

    flavorDimensions += "type"
    productFlavors {
        create("dev") {
            dimension = "type"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "SDAI Dev")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"FOSS\"")
        }
        create("foss") {
            dimension = "type"
            applicationIdSuffix = ".foss"
            resValue("string", "app_name", "SDAI FOSS")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"FOSS\"")
        }
        create("playstore") {
            dimension = "type"
            resValue("string", "app_name", "SDAI")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"GOOGLE_PLAY\"")
        }
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

kapt {
    correctErrorTypes = true
}

/**
 * Workaround for "Bug: baseline.profm not deterministic"
 *
 * Reference     : https://f-droid.org/docs/Reproducible_Builds/#bug-baselineprofm-not-deterministic
 * Fix snippet   : https://gist.github.com/obfusk/61046e09cee352ae6dd109911534b12e
 * Issue tracker : https://issuetracker.google.com/issues/231837768
 */
project.afterEvaluate {
    tasks.forEach { task ->
        if (task.name.startsWith("compile") && task.name.endsWith("ReleaseArtProfile")) {
            task.doLast {
                outputs.files.forEach { file ->
                    if (file.name.endsWith(".profm")) {
                        println("Sorting $file ...")
                        val version = ArtProfileSerializer.valueOf("METADATA_0_0_2")
                        val profile = ArtProfile(file)
                        val keys = ArrayList(profile?.profileData?.keys ?: setOf())
                        val sortedData = LinkedHashMap<DexFile, DexFileData>()
                        Collections.sort(keys, DexFile.Companion)

                        keys.forEach { key ->
                            profile?.profileData?.get(key)?.let { value ->
                                sortedData[key] = value
                            }
                        }

                        FileOutputStream(file).use { stream ->
                            stream.write(version.magicBytes)
                            stream.write(version.versionBytes)
                            version.write(stream, sortedData, "")
                        }
                    }
                }
            }
        }
    }
}
