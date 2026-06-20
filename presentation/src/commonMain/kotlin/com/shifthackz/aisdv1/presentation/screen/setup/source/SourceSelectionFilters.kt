package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.ui.graphics.Color
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.ServerSourceReadiness
import com.shifthackz.aisdv1.domain.entity.ServerSourceType
import com.shifthackz.aisdv1.presentation.model.readinessFor
import com.shifthackz.aisdv1.presentation.screen.setup.component.subtitle
import com.shifthackz.aisdv1.presentation.screen.setup.component.title
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState

/**
 * Applies provider search, filters, and sort order to the available source list.
 *
 * @param strings localized source labels included in search matching.
 * @return sources visible on the provider selection step.
 * @author Dmitriy Moroz
 */
internal fun ServerSetupState.visibleSources(strings: ServerSetupStrings): List<ServerSource> =
    allowedModes
        .filter { source -> source.matchesFilters(this, strings) }
        .let { sources ->
            when (sourceSortOrder) {
                ServerSetupState.SourceSortOrder.DEFAULT -> sources
                ServerSetupState.SourceSortOrder.RECENTLY_UPDATED -> sources.sortedByDescending {
                    it.versionSortKey()
                }

                ServerSetupState.SourceSortOrder.OLDEST_UPDATED -> sources.sortedBy {
                    it.versionSortKey()
                }
            }
        }

/**
 * Checks whether selecting [type] can still produce at least one provider.
 *
 * @param type provider category to test against current readiness/tag filters.
 * @author Dmitriy Moroz
 */
internal fun ServerSetupState.isSourceTypeEnabled(type: ServerSourceType): Boolean =
    sourceTypeFilter == type ||
        allowedModes.hasSourceFilterMatch(this, type, sourceReadinessFilters, sourceTagFilters)

/**
 * Checks whether adding [readiness] keeps the filter set non-empty.
 *
 * @param readiness provider readiness value to test.
 * @author Dmitriy Moroz
 */
internal fun ServerSetupState.isSourceReadinessEnabled(readiness: ServerSourceReadiness): Boolean =
    readiness in sourceReadinessFilters ||
        allowedModes.hasSourceFilterMatch(
            this,
            sourceTypeFilter,
            sourceReadinessFilters + readiness,
            sourceTagFilters,
        )

/**
 * Checks whether adding [tag] keeps the filter set non-empty.
 *
 * @param tag provider capability tag to test.
 * @author Dmitriy Moroz
 */
internal fun ServerSetupState.isSourceTagEnabled(tag: FeatureTag): Boolean =
    tag in sourceTagFilters ||
        allowedModes.hasSourceFilterMatch(
            this,
            sourceTypeFilter,
            sourceReadinessFilters,
            sourceTagFilters + tag,
        )

internal fun ServerSourceType.mapToUi(strings: ServerSetupStrings): String = when (this) {
    ServerSourceType.SELF_HOSTED -> strings.sourceFilterSelfHosted
    ServerSourceType.CLOUD -> strings.sourceFilterCloud
    ServerSourceType.LOCAL -> strings.sourceFilterLocal
}

internal fun ServerSourceReadiness.mapToUi(strings: ServerSetupStrings): String = when (this) {
    ServerSourceReadiness.EXPERIMENTAL -> strings.readinessExperimental
    ServerSourceReadiness.ALPHA -> strings.readinessAlpha
    ServerSourceReadiness.BETA -> strings.readinessBeta
    ServerSourceReadiness.STABLE -> strings.readinessStable
}

internal fun ServerSetupState.SourceSortOrder.mapToUi(strings: ServerSetupStrings): String = when (this) {
    ServerSetupState.SourceSortOrder.DEFAULT -> strings.sourceSortDefault
    ServerSetupState.SourceSortOrder.RECENTLY_UPDATED -> strings.sourceSortRecent
    ServerSetupState.SourceSortOrder.OLDEST_UPDATED -> strings.sourceSortOldest
}

internal fun ServerSourceReadiness.containerColor(): Color = when (this) {
    ServerSourceReadiness.EXPERIMENTAL -> Color(0xFFD32F2F)
    ServerSourceReadiness.ALPHA -> Color(0xFFF57C00)
    ServerSourceReadiness.BETA -> Color(0xFFFFD54F)
    ServerSourceReadiness.STABLE -> Color(0xFF388E3C)
}

internal fun ServerSourceReadiness.contentColor(): Color = when (this) {
    ServerSourceReadiness.BETA -> Color(0xFF3B2F00)
    else -> Color.White
}

private fun ServerSource.matchesFilters(
    state: ServerSetupState,
    strings: ServerSetupStrings,
): Boolean {
    val typeFilter = state.sourceTypeFilter
    if (typeFilter != null && type != typeFilter) {
        return false
    }
    val sourceReadiness = readinessFor(state.platform)
    if (state.sourceReadinessFilters.isNotEmpty() && sourceReadiness !in state.sourceReadinessFilters) {
        return false
    }
    if (state.sourceTagFilters.isNotEmpty() && !featureTags.containsAll(state.sourceTagFilters)) {
        return false
    }
    val queryWords = state.sourceSearchQuery
        .trim()
        .lowercase()
        .split(Regex("\\s+"))
        .filter(String::isNotBlank)
    if (queryWords.isEmpty()) {
        return true
    }
    val searchableText = buildList {
        add(key)
        add(title(strings, state.platform))
        add(subtitle(strings, state.platform))
        add(type.mapToUi(strings))
        add(sourceReadiness.mapToUi(strings))
        add(version)
        featureTags.forEach { tag -> add(tag.mapToUi()) }
    }
        .joinToString(separator = " ")
        .lowercase()
    return queryWords.all(searchableText::contains)
}

private fun List<ServerSource>.hasSourceFilterMatch(
    state: ServerSetupState,
    type: ServerSourceType?,
    readinessFilters: Set<ServerSourceReadiness>,
    tags: Set<FeatureTag>,
): Boolean = any { source ->
    val sourceReadiness = source.readinessFor(state.platform)
    (type == null || source.type == type) &&
        (readinessFilters.isEmpty() || sourceReadiness in readinessFilters) &&
        source.featureTags.containsAll(tags)
}

/**
 * Converts the date-like provider version format (`yyyy.M.d[-tag]`) to a sortable key.
 *
 * Unknown or malformed version parts are treated as zero, keeping the sort stable
 * without failing provider selection UI.
 *
 * @author Dmitriy Moroz
 */
private fun ServerSource.versionSortKey(): Int {
    val date = version.substringBefore("-")
    val parts = date.split(".")
    val year = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val month = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val day = parts.getOrNull(2)?.toIntOrNull() ?: 0
    return year * 10_000 + month * 100 + day
}
