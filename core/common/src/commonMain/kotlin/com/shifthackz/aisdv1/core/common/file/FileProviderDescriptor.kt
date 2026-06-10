package com.shifthackz.aisdv1.core.common.file

const val LOCAL_DIFFUSION_CUSTOM_PATH = "/storage/emulated/0/Download/SDAI/model"

interface FileProviderDescriptor {
    val providerPath: String
    val imagesCacheDirPath: String
    val logsCacheDirPath: String
    val localModelDirPath: String
    val workCacheDirPath: String
}
