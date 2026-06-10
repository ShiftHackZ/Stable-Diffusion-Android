package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.DownloadState
import kotlinx.coroutines.flow.Flow

interface DownloadModelUseCase {
    operator fun invoke(id: String, url: String): Flow<DownloadState>
}
