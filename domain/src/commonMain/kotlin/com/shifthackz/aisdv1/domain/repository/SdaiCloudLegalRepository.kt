package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.SdaiCloudTerms

interface SdaiCloudLegalRepository {
    suspend fun getTerms(): SdaiCloudTerms
}

object NoOpSdaiCloudLegalRepository : SdaiCloudLegalRepository {
    override suspend fun getTerms(): SdaiCloudTerms = SdaiCloudTerms()
}
