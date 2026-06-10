import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.localization"
}

private val localizationPackage = "com.shifthackz.aisdv1.core.localization"
private val defaultLanguageCode = "en"
private val preferredLanguageOrder = listOf(defaultLanguageCode, "uk", "tr", "ru", "zh")
private val stringsResDir = layout.projectDirectory.dir("src/androidMain/res")
private val generatedLocalizationDir = layout.buildDirectory.dir("generated/source/localizationCatalog/commonMain/kotlin")

private val generateLocalizationCatalog by tasks.registering {
    val stringsFiles = fileTree(stringsResDir) {
        include("values*/strings.xml")
    }
    inputs.files(stringsFiles)
    outputs.dir(generatedLocalizationDir)

    doLast {
        val languageStrings = stringsFiles.files
            .sortedBy { it.languageCode() }
            .associate { file ->
                file.languageCode() to file.parseAndroidStrings()
            }
            .toMutableMap()

        val defaultStrings = languageStrings[defaultLanguageCode]
            ?: error("Missing default localization file src/androidMain/res/values/strings.xml")

        val languages = languageStrings.keys
            .sortedWith(compareBy({ preferredLanguageOrder.indexOf(it).takeIf { index -> index >= 0 } ?: Int.MAX_VALUE }, { it }))

        val outputFile = generatedLocalizationDir.get()
            .file("com/shifthackz/aisdv1/core/localization/LocalizationCatalog.kt")
            .asFile

        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            buildString {
                appendLine("package $localizationPackage")
                appendLine()
                appendLine("internal val localizationLanguages: List<LocalizationLanguage> = listOf(")
                languages.forEach { code ->
                    val strings = languageStrings.getValue(code)
                    val languageName = strings["language"] ?: code
                    appendLine("    LocalizationLanguage(code = ${code.kotlinLiteral()}, name = ${languageName.kotlinLiteral()}),")
                }
                appendLine(")")
                appendLine()
                appendStringsMap("enStrings", defaultStrings)
                languages
                    .filterNot { it == defaultLanguageCode }
                    .forEach { code ->
                        appendLine()
                        appendStringsMap("${code.sanitizedMapName()}Strings", languageStrings.getValue(code))
                    }
                appendLine()
                appendLine("internal val localizationCatalog: Map<String, Map<String, String>> = mapOf(")
                languages.forEach { code ->
                    val mapName = "${code.sanitizedMapName()}Strings"
                    val value = if (code == defaultLanguageCode) mapName else "enStrings + $mapName"
                    appendLine("    ${code.kotlinLiteral()} to $value,")
                }
                appendLine(")")
            },
        )
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateLocalizationCatalog)
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

private fun File.languageCode(): String {
    val qualifier = parentFile.name.removePrefix("values")
    return qualifier
        .removePrefix("-")
        .takeIf(String::isNotBlank)
        ?.substringBefore("-")
        ?: defaultLanguageCode
}

private fun File.parseAndroidStrings(): Map<String, String> {
    val factory = DocumentBuilderFactory.newInstance().apply {
        isIgnoringComments = true
        isCoalescing = true
    }
    val document = factory.newDocumentBuilder().parse(this)
    val nodes = document.getElementsByTagName("string")
    return buildMap {
        repeat(nodes.length) { index ->
            val element = nodes.item(index) as Element
            val name = element.getAttribute("name")
            if (name.isNotBlank()) {
                put(name, element.textContent.normalizeAndroidString())
            }
        }
    }
}

private fun StringBuilder.appendStringsMap(name: String, strings: Map<String, String>) {
    appendLine("private val $name: Map<String, String> = mapOf(")
    strings.forEach { (key, value) ->
        appendLine("    ${key.kotlinLiteral()} to ${value.kotlinLiteral()},")
    }
    appendLine(")")
}

private fun String.normalizeAndroidString(): String = this
    .replace("\\\\n", "\n")
    .replace("\\n", "\n")
    .replace("\\'", "'")
    .replace("\\\"", "\"")

private fun String.sanitizedMapName(): String =
    replace(Regex("[^A-Za-z0-9_]"), "_")

private fun String.kotlinLiteral(): String = buildString {
    append('"')
    this@kotlinLiteral.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '$' -> append("\\$")
            else -> append(char)
        }
    }
    append('"')
}
