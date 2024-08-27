package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import io.reactivex.rxjava3.core.Single

internal class ConnectToLocalDiffusionUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToLocalDiffusionUseCase {

    override fun invoke(modelId: String) = getConfigurationUseCase()
        .map { originalConfiguration ->
            originalConfiguration.copy(
                source = ServerSource.LOCAL_MICROSOFT_ONNX,
                localOnnxModelId = modelId,
            )
        }
        .flatMapCompletable(setServerConfigurationUseCase::invoke)
        .andThen(Single.just(Result.success(Unit)))
        .onErrorResumeNext { t -> Single.just(Result.failure(t)) }
}
