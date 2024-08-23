import com.android.build.api.dsl.ApplicationExtension
import com.shifthackz.aisdv1.buildlogic.configureApplication
import com.shifthackz.aisdv1.buildlogic.configureCompose
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
                apply("org.jetbrains.kotlin.kapt")
                apply("generic.jacoco")
            }

            extensions.configure<ApplicationExtension> {
                configureApplication(this)
                configureCompose(this)
                defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
            }
        }
    }
}
