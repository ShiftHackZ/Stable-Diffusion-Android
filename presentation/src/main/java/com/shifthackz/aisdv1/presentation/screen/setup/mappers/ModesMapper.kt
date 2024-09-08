package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource

val BuildInfoProvider.allowedModes: List<ServerSource>
    get() = ServerSource
        .entries
        .filter { it.allowedInBuilds.contains(type) }
