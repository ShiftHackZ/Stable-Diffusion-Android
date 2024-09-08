import com.android.build.gradle.BaseExtension
import com.shifthackz.aisdv1.buildlogic.jacocoCodeCoverageReporting
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

class JacocoConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("jacoco")
            }

            tasks.withType<Test> {
                configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }

            extensions.configure<BaseExtension> {
                target.afterEvaluate {
                    jacocoCodeCoverageReporting(this@configure)
                }
            }
        }
    }
}
