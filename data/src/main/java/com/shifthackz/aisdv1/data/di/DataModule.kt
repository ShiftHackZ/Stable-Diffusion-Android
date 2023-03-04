package com.shifthackz.aisdv1.data.di

val dataModule = (remoteDataSourceModule + localDataSourceModule + repositoryModule)
    .toTypedArray()
