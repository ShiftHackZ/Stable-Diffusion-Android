package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Single

interface GetLocalOnnxModelsUseCase {
    operator fun invoke(): Single<List<LocalAiModel>>
}
