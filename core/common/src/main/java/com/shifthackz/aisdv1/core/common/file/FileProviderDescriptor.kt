package com.shifthackz.aisdv1.core.common.file

interface FileProviderDescriptor {
    val providerPath: String
    val imagesCacheDirPath: String
    val logsCacheDirPath: String
    val localModelDirPath: String
    val workCacheDirPath: String
}
