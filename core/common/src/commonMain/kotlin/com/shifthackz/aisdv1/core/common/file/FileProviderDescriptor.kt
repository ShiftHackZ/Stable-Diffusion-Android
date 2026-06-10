package com.shifthackz.aisdv1.core.common.file

/**
 * Exposes the `LOCAL_DIFFUSION_CUSTOM_PATH` value used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
const val LOCAL_DIFFUSION_CUSTOM_PATH = "/storage/emulated/0/Download/SDAI/model"

/**
 * Defines the `FileProviderDescriptor` contract for the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
interface FileProviderDescriptor {
    /**
     * Exposes the `providerPath` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val providerPath: String
    /**
     * Exposes the `imagesCacheDirPath` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val imagesCacheDirPath: String
    /**
     * Exposes the `logsCacheDirPath` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val logsCacheDirPath: String
    /**
     * Exposes the `localModelDirPath` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val localModelDirPath: String
    /**
     * Exposes the `workCacheDirPath` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val workCacheDirPath: String
}
