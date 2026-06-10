package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.dao.GenerationResultDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Coordinates `GenerationResultLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class GenerationResultLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: GenerationResultDao,
) : GenerationResultDataSource.Local {

    /**
     * Performs the SDAI side effect handled by `insert`.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `insert`.
     * @author Dmitriy Moroz
     */
    override suspend fun insert(result: AiGenerationResult): Long =
        dao.insert(result.mapDomainToEntity())

    /**
     * Executes the `queryAll` step in the SDAI data layer.
     *
     * @return Result produced by `queryAll`.
     * @author Dmitriy Moroz
     */
    override suspend fun queryAll(): List<AiGenerationResult> =
        dao.query().mapEntityToDomain()

    /**
     * Executes the `queryPage` step in the SDAI data layer.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `queryPage`.
     * @author Dmitriy Moroz
     */
    override suspend fun queryPage(limit: Int, offset: Int): List<AiGenerationResult> =
        dao.queryPage(limit, offset).mapEntityToDomain()

    /**
     * Loads SDAI data through `observePage`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observePage`.
     * @author Dmitriy Moroz
     */
    override fun observePage(limit: Int, offset: Int): Flow<List<AiGenerationResult>> =
        dao.observePage(limit, offset).map { it.mapEntityToDomain() }

    /**
     * Loads SDAI data through `observeCount`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeCount(): Flow<Int> = dao.observeCount()

    /**
     * Executes the `queryById` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `queryById`.
     * @author Dmitriy Moroz
     */
    override suspend fun queryById(id: Long): AiGenerationResult =
        dao.queryById(id).mapEntityToDomain()

    /**
     * Executes the `queryByIdList` step in the SDAI data layer.
     *
     * @param idList id list value consumed by the API.
     * @return Result produced by `queryByIdList`.
     * @author Dmitriy Moroz
     */
    override suspend fun queryByIdList(idList: List<Long>): List<AiGenerationResult> =
        dao.queryByIdList(idList).mapEntityToDomain()

    /**
     * Performs the SDAI side effect handled by `deleteById`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    /**
     * Performs the SDAI side effect handled by `deleteByIdList`.
     *
     * @param idList id list value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun deleteByIdList(idList: List<Long>) {
        dao.deleteByIdList(idList)
    }

    /**
     * Performs the SDAI side effect handled by `deleteAll`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
