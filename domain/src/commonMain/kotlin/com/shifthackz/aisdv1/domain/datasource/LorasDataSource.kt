package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `LorasDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface LorasDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Remote : LorasDataSource {

        /**
         * Defines the `Automatic1111` contract for the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        interface Automatic1111 : Remote {
            /**
             * Loads SDAI data through `fetchLoras`.
             *
             * @param baseUrl base url value consumed by the API.
             * @param credentials credentials value consumed by the API.
             * @return Result produced by `fetchLoras`.
             * @author Dmitriy Moroz
             */
            suspend fun fetchLoras(
                baseUrl: String,
                credentials: AuthorizationCredentials,
            ): List<LoRA>
        }

        /**
         * Defines the `SwarmUi` contract for the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        interface SwarmUi : Remote {
            /**
             * Loads SDAI data through `fetchLoras`.
             *
             * @param baseUrl base url value consumed by the API.
             * @param sessionId session id value consumed by the API.
             * @param credentials credentials value consumed by the API.
             * @return Result produced by `fetchLoras`.
             * @author Dmitriy Moroz
             */
            suspend fun fetchLoras(
                baseUrl: String,
                sessionId: String,
                credentials: AuthorizationCredentials,
            ): List<LoRA>
        }
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : LorasDataSource {
        /**
         * Loads SDAI data through `getLoras`.
         *
         * @return Result produced by `getLoras`.
         * @author Dmitriy Moroz
         */
        suspend fun getLoras(): List<LoRA>
        /**
         * Performs the SDAI side effect handled by `insertLoras`.
         *
         * @param loras loras value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertLoras(loras: List<LoRA>)
    }
}
