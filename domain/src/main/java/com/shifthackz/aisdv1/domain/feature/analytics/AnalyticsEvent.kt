package com.shifthackz.aisdv1.domain.feature.analytics

abstract class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = mapOf(),
) {
    val isValid: Boolean
        get() = name.isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnalyticsEvent) return false

        if (name != other.name) return false
        if (parameters != other.parameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + parameters.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        val tag = AnalyticsEvent::class.simpleName
        appendLine("$tag - [$name]")
        parameters.forEach { (key, value) ->
            appendLine("$tag - $key: $value")
        }
    }
}
