package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface LorasDataSource {

    sealed interface Remote : LorasDataSource {

        interface Automatic1111 : Remote {
            suspend fun fetchLoras(
                baseUrl: String,
                credentials: AuthorizationCredentials,
            ): List<LoRA>
        }

        interface SwarmUi : Remote {
            suspend fun fetchLoras(
                baseUrl: String,
                sessionId: String,
                credentials: AuthorizationCredentials,
            ): List<LoRA>
        }
    }

    interface Local : LorasDataSource {
        suspend fun getLoras(): List<LoRA>
        suspend fun insertLoras(loras: List<LoRA>)
    }
}
