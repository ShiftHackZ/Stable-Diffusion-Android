package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single

internal class GetAllGalleryUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetAllGalleryUseCase {

    override operator fun invoke(): Single<List<AiGenerationResult>> = repository.getAll()
}
