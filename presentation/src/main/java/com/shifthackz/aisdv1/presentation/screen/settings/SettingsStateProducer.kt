package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.common.BuildInfoProvider
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import io.reactivex.rxjava3.core.Single

class SettingsStateProducer(
    private val buildInfoProvider: BuildInfoProvider,
    private val getStableDiffusionModelsUseCase: GetStableDiffusionModelsUseCase,
) {
    operator fun invoke(): Single<SettingsState> = Single.zip(
        Single.fromCallable { buildInfoProvider.toString() },
        getStableDiffusionModelsUseCase(),
    ) { version, modelData ->
        SettingsState.Content(
            sdModels = modelData.map { (model, _) -> model.title },
            sdModelSelected = modelData.firstOrNull { it.second }?.first?.title ?: "",
            appVersion = version,
        )
    }
}
