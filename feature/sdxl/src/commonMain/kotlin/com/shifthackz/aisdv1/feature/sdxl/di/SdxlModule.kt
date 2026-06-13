package com.shifthackz.aisdv1.feature.sdxl.di

import org.koin.core.module.Module

val sdxlModule: Module = platformSdxlModule()

internal expect fun platformSdxlModule(): Module
