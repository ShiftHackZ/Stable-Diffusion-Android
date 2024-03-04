package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import io.reactivex.rxjava3.core.Flowable

class SettingsStateProducer(
    getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
    private val buildInfoProvider: BuildInfoProvider,
    private val preferenceManager: PreferenceManager,
) {

    private val appVersionProducer = Flowable.fromCallable { buildInfoProvider.toString() }

    private val sdModelsProducer = getStableDiffusionModelsUseCase()
        .toFlowable()
        .onErrorReturn { emptyList() }

    operator fun invoke(): Flowable<SettingsState> = Flowable.combineLatest(
        appVersionProducer,
        sdModelsProducer,
        preferenceManager.observe(),
    ) { version, modelData, settings ->
        SettingsState(
            loading = false,
            sdModels = modelData.map { (model, _) -> model.title },
            sdModelSelected = modelData.firstOrNull { it.second }?.first?.title ?: "",
            localUseNNAPI = settings.localUseNNAPI,
            monitorConnectivity = settings.monitorConnectivity,
            autoSaveAiResults = settings.autoSaveAiResults,
            saveToMediaStore = settings.saveToMediaStore,
            formAdvancedOptionsAlwaysShow = settings.formAdvancedOptionsAlwaysShow,
            formPromptTaggedInput = settings.formPromptTaggedInput,
            appVersion = version,
            showLocalUseNNAPI = settings.source == ServerSource.LOCAL,
            showSdModelSelector = settings.source == ServerSource.AUTOMATIC1111,
            showMonitorConnectionOption = settings.source == ServerSource.AUTOMATIC1111,
            showFormAdvancedOption = settings.source != ServerSource.OPEN_AI,
        )
    }
}
