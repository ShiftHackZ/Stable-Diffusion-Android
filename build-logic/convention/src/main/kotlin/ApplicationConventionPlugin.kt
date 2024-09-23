import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import com.shifthackz.aisdv1.buildlogic.configureApplication
import com.shifthackz.aisdv1.buildlogic.configureCompose
import com.shifthackz.aisdv1.buildlogic.configureFlavors
import com.shifthackz.aisdv1.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("generic.jacoco")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<ApplicationExtension> {
                configureApplication(this)
                configureCompose(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
            }
            extensions.configure<BaseExtension> {
                configureFlavors(this)
            }
        }
    }
}
