package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.DownloadState
import io.reactivex.rxjava3.core.Observable

interface DownloadModelUseCase {
    operator fun invoke(): Observable<DownloadState>
}
