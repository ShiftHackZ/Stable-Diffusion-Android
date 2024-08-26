import com.android.build.gradle.LibraryExtension
import com.shifthackz.aisdv1.buildlogic.configureFlavorsCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class FlavorsConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                configureFlavorsCommon(this)
            }
        }
    }
}
