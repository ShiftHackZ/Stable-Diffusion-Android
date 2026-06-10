import com.android.build.gradle.LibraryExtension
import com.shifthackz.aisdv1.buildlogic.configureKotlinAndroid
import com.shifthackz.aisdv1.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("generic.jacoco")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                sourceSets.getByName("main") {
                    manifest.srcFile("src/androidMain/AndroidManifest.xml")
                    res.srcDirs("src/androidMain/res")
                    assets.srcDirs("src/androidMain/assets")
                    resources.srcDirs("src/androidMain/resources")
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget()
                iosArm64()
                iosSimulatorArm64()
            }
        }
    }
}
