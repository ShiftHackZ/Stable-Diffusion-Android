@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.tools.profgen.ArtProfile
import com.android.tools.profgen.ArtProfileSerializer
import com.android.tools.profgen.DexFile
import com.android.tools.profgen.DexFileData
import java.io.FileOutputStream
import java.util.Collections

/**
 * Workaround for "Bug: baseline.profm not deterministic"
 *
 * Reference     : https://f-droid.org/docs/Reproducible_Builds/#bug-baselineprofm-not-deterministic
 * Fix snippet   : https://gist.github.com/obfusk/61046e09cee352ae6dd109911534b12e
 * Issue tracker : https://issuetracker.google.com/issues/231837768
 */
class BaselineProFmConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.afterEvaluate {
            tasks.forEach { task ->
                if (task.name.startsWith("compile") && task.name.endsWith("ReleaseArtProfile")) {
                    task.doLast {
                        outputs.files.forEach { file ->
                            if (file.name.endsWith(".profm")) {
                                println("[Baseline ProFm] -> Trying to sort: $file")
                                val version = ArtProfileSerializer.valueOf("METADATA_0_0_2")
                                val profile = ArtProfile(file)
                                val keys = ArrayList(profile?.profileData?.keys ?: setOf())
                                val sortedData = LinkedHashMap<DexFile, DexFileData>()
                                println("[Baseline ProFm] -> Keys: $keys")
                                Collections.sort(keys, DexFile.Companion)
                                println("[Baseline ProFm] -> Keys (sorted): $keys")

                                keys.forEach { key ->
                                    profile?.profileData?.get(key)?.let { value ->
                                        sortedData[key] = value
                                    }
                                }
                                println("[Baseline ProFm] -> Sorted data: $sortedData")

                                FileOutputStream(file).use { stream ->
                                    println("[Baseline ProFm] -> Writing magic bytes...")
                                    stream.write(version.magicBytes)
                                    println("[Baseline ProFm] -> Writing version bytes...")
                                    stream.write(version.versionBytes)
                                    println("[Baseline ProFm] -> Writing sorted data...")
                                    version.write(stream, sortedData, "")
                                }
                                println("[Baseline ProFm] -> Done!")
                            }
                        }
                    }
                }
            }
        }
    }
}
