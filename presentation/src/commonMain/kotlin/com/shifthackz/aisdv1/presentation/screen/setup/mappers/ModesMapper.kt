package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.isAvailableOn

/**
 * Flavor-aware provider list before platform-specific availability filtering is applied.
 */
val BuildInfoProvider.allowedModes: List<ServerSource>
    get() = ServerSource
        .entries
        .filter { source ->
            source.allowedInBuilds.contains(type) &&
                source.isAvailableOn(platform)
        }
