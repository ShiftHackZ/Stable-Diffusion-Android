package com.shifthackz.aisdv1.feature.auth.di

import org.koin.core.module.Module

val authModule: Module = platformAuthModule()

internal expect fun platformAuthModule(): Module
