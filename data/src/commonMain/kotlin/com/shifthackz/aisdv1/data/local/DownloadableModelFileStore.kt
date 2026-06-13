package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Defines the `DownloadableModelFileStore` contract for the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal interface DownloadableModelFileStore {
    /**
     * Executes the `isDownloaded` step in the SDAI data layer.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `isDownloaded`.
     * @author Dmitriy Moroz
     */
    fun isDownloaded(model: LocalAiModel): Boolean
    /**
     * Loads SDAI data through `resolvePath`.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `resolvePath`.
     * @author Dmitriy Moroz
     */
    fun resolvePath(model: LocalAiModel): String
    /**
     * Loads SDAI data through `resolveSingleFilePath`.
     *
     * @param path raw file or directory path used by the operation.
     * @return Result produced by `resolveSingleFilePath`.
     * @author Dmitriy Moroz
     */
    fun resolveSingleFilePath(path: String): String
    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    fun delete(id: String)
}

/**
 * Provides the `NoOpDownloadableModelFileStore` singleton used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal object NoOpDownloadableModelFileStore : DownloadableModelFileStore {

    /**
     * Executes the `isDownloaded` step in the SDAI data layer.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `isDownloaded`.
     * @author Dmitriy Moroz
     */
    override fun isDownloaded(model: LocalAiModel): Boolean =
        model.id == LocalAiModel.CustomOnnx.id ||
            model.id == LocalAiModel.CustomMediaPipe.id ||
            model.id == LocalAiModel.CustomSdxl.id ||
            model.id == LocalAiModel.CustomCoreMl.id

    /**
     * Loads SDAI data through `resolvePath`.
     *
     * @param model model value consumed by the API.
     * @return Result produced by `resolvePath`.
     * @author Dmitriy Moroz
     */
    override fun resolvePath(model: LocalAiModel): String = ""

    /**
     * Loads SDAI data through `resolveSingleFilePath`.
     *
     * @param path raw file or directory path used by the operation.
     * @return Result produced by `resolveSingleFilePath`.
     * @author Dmitriy Moroz
     */
    override fun resolveSingleFilePath(path: String): String = path

    /**
     * Performs the SDAI side effect handled by `delete`.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override fun delete(id: String) = Unit
}
