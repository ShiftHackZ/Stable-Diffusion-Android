package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Implements `ObserveLocalOnnxModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ObserveLocalOnnxModelsUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: DownloadableModelRepository,
) : ObserveLocalOnnxModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = repository
        .observeAllOnnx()
        .distinctUntilChanged()
}
