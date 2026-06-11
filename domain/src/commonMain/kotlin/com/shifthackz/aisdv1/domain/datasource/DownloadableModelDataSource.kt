package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `DownloadableModelDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DownloadableModelDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : DownloadableModelDataSource {
        /**
         * Loads SDAI data through `fetch`.
         *
         * @return Result produced by `fetch`.
         * @author Dmitriy Moroz
         */
        suspend fun fetch(): List<LocalAiModel>
        /**
         * Executes the `download` step in the SDAI domain layer.
         *
         * @param id identifier of the target entity.
         * @param url remote URL used by the operation.
         * @return Result produced by `download`.
         * @author Dmitriy Moroz
         */
        fun download(id: String, url: String): Flow<DownloadState>
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : DownloadableModelDataSource {
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
         * Loads SDAI data through `getAllCoreMl`.
         *
         * @return Result produced by `getAllCoreMl`.
         * @author Dmitriy Moroz
         */
        suspend fun getAllCoreMl(): List<LocalAiModel>
        /**
         * Loads SDAI data through `getById`.
         *
         * @param id identifier of the target entity.
         * @return Result produced by `getById`.
         * @author Dmitriy Moroz
         */
        suspend fun getById(id: String): LocalAiModel
        /**
         * Loads SDAI data through `getSelectedOnnx`.
         *
         * @return Result produced by `getSelectedOnnx`.
         * @author Dmitriy Moroz
         */
        suspend fun getSelectedOnnx(): LocalAiModel
        /**
         * Loads SDAI data through `getSelectedCoreMl`.
         *
         * @return Result produced by `getSelectedCoreMl`.
         * @author Dmitriy Moroz
         */
        suspend fun getSelectedCoreMl(): LocalAiModel
        /**
         * Loads SDAI data through `observeAllOnnx`.
         *
         * @return Result produced by `observeAllOnnx`.
         * @author Dmitriy Moroz
         */
        fun observeAllOnnx(): Flow<List<LocalAiModel>>
        /**
         * Loads SDAI data through `observeAllCoreMl`.
         *
         * @return Result produced by `observeAllCoreMl`.
         * @author Dmitriy Moroz
         */
        fun observeAllCoreMl(): Flow<List<LocalAiModel>>
        /**
         * Performs the SDAI side effect handled by `save`.
         *
         * @param list list value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun save(list: List<LocalAiModel>)
        /**
         * Performs the SDAI side effect handled by `delete`.
         *
         * @param id identifier of the target entity.
         * @author Dmitriy Moroz
         */
        suspend fun delete(id: String)
    }
}
