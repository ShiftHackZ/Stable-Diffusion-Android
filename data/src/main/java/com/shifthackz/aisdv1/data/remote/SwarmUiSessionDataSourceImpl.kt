package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_SESSION
import com.shifthackz.aisdv1.network.exception.BadSessionException
import io.reactivex.rxjava3.core.Single

internal class SwarmUiSessionDataSourceImpl(
    private val api: SwarmUiApi,
    private val sessionPreference: SessionPreference,
    private val serverUrlProvider: ServerUrlProvider,
) : SwarmUiSessionDataSource {

    override fun getSessionId(connectUrl: String?): Single<String> =
        if (sessionPreference.swarmUiSessionId.isBlank()) {
            forceRenew(connectUrl)
        } else {
            Single.just(sessionPreference.swarmUiSessionId)
        }

    override fun forceRenew(connectUrl: String?): Single<String> {
        val chain = connectUrl
            ?.let { url -> "$url/$PATH_SESSION".fixUrlSlashes() }
            ?.let(api::getNewSession)
            ?: serverUrlProvider(PATH_SESSION).flatMap(api::getNewSession)

        return chain
            .flatMap { response ->
                response.sessionId
                    ?.takeIf(String::isNotBlank)
                    ?.let { Single.just(it) }
                    ?: Single.error(IllegalStateException("Bad session ID."))
            }
            .map { sessionId ->
                sessionPreference.swarmUiSessionId = sessionId
                sessionId
            }
    }

    override fun <T : Any> handleSessionError(chain: Single<T>): Single<T> = chain.onErrorResumeNext { t ->
        if (t is BadSessionException) {
            forceRenew().flatMap { chain }
        } else {
            Single.error(t)
        }
    }
}
