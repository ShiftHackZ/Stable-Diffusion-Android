package com.shifthackz.aisdv1.app.di

import android.content.Context
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val KEY_PREFERENCE_MANAGER = "aisdv1_preference_manager"

val preferenceModule = module {

    single<PreferenceManager> {
        androidContext()
            .getSharedPreferences(KEY_PREFERENCE_MANAGER, Context.MODE_PRIVATE)
            .let(::PreferenceManagerImpl)
    }
}
