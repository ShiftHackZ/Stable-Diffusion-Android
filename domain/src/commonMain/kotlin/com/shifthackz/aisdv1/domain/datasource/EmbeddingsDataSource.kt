package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `EmbeddingsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface EmbeddingsDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Remote : EmbeddingsDataSource {

        /**
         * Defines the `Automatic1111` contract for the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        interface Automatic1111 : Remote {
            /**
             * Loads SDAI data through `fetchEmbeddings`.
             *
             * @param baseUrl base url value consumed by the API.
             * @param credentials credentials value consumed by the API.
             * @return Result produced by `fetchEmbeddings`.
             * @author Dmitriy Moroz
             */
            suspend fun fetchEmbeddings(
                baseUrl: String,
                credentials: AuthorizationCredentials,
            ): List<Embedding>
        }

        /**
         * Defines the `SwarmUi` contract for the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        interface SwarmUi : Remote {
            /**
             * Loads SDAI data through `fetchEmbeddings`.
             *
             * @param baseUrl base url value consumed by the API.
             * @param sessionId session id value consumed by the API.
             * @param credentials credentials value consumed by the API.
             * @return Result produced by `fetchEmbeddings`.
             * @author Dmitriy Moroz
             */
            suspend fun fetchEmbeddings(
                baseUrl: String,
                sessionId: String,
                credentials: AuthorizationCredentials,
            ): List<Embedding>
        }
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : EmbeddingsDataSource {
        /**
         * Loads SDAI data through `getEmbeddings`.
         *
         * @return Result produced by `getEmbeddings`.
         * @author Dmitriy Moroz
         */
        suspend fun getEmbeddings(): List<Embedding>
        /**
         * Performs the SDAI side effect handled by `insertEmbeddings`.
         *
         * @param list list value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun insertEmbeddings(list: List<Embedding>)
    }
}
