package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import io.reactivex.rxjava3.core.Single

class SettingsStateProducer(
    private val buildInfoProvider: BuildInfoProvider,
    private val getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val preferenceManager: PreferenceManager,
) {

    private val appVersionProducer = Single.fromCallable { buildInfoProvider.toString() }

    private val sdModelsProducer = getStableDiffusionModelsUseCase()
        .onErrorReturn { emptyList() }

    operator fun invoke(): Single<SettingsState> = Single.zip(
        appVersionProducer,
        sdModelsProducer,
    ) { version, modelData ->
        SettingsState.Content(
            sdModels = modelData.map { (model, _) -> model.title },
            sdModelSelected = modelData.firstOrNull { it.second }?.first?.title ?: "",
            autoSaveAiResults = preferenceManager.autoSaveAiResults,
            appVersion = version,
            showRateGooglePlay = buildInfoProvider.buildType == BuildType.GOOGLE_PLAY,
        )
    }/*.onErrorResumeNext {
        Single.just(
            SettingsState.Content(
                sdModels = emptyList(),
                sdModelSelected = "Unknown",
                appVersion = "Unknown",
                localization = Localization.ENGLISH
            ) as SettingsState
        )
    }*/
}
