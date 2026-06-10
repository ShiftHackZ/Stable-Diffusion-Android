package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `DownloadableModelRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DownloadableModelRepository {
    /**
     * Executes the `download` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @return Result produced by `download`.
     * @author Dmitriy Moroz
     */
    fun download(id: String, url: String): Flow<DownloadState>
    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    suspend fun delete(id: String)
    /**
     * Loads SDAI data through `getAllOnnx`.
     *
     * @return Result produced by `getAllOnnx`.
     * @author Dmitriy Moroz
     */
    suspend fun getAllOnnx(): List<LocalAiModel>
    /**
     * Loads SDAI data through `getAllMediaPipe`.
     *
     * @return Result produced by `getAllMediaPipe`.
     * @author Dmitriy Moroz
     */
    suspend fun getAllMediaPipe(): List<LocalAiModel>
    /**
     * Loads SDAI data through `observeAllOnnx`.
     *
     * @return Result produced by `observeAllOnnx`.
     * @author Dmitriy Moroz
     */
    fun observeAllOnnx(): Flow<List<LocalAiModel>>
}
