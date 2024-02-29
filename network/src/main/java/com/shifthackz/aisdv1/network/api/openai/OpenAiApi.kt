package com.shifthackz.aisdv1.network.api.openai

import com.shifthackz.aisdv1.network.request.OpenAiRequest
import com.shifthackz.aisdv1.network.response.OpenAiResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OpenAiApi {

    /**
     * Tries retrieve a models list to see if api-key is valid
     *
     * Reference: https://stackoverflow.com/questions/76522693/how-to-check-the-validity-of-the-openai-key-from-python
     * Documentation: https://platform.openai.com/docs/api-reference/models
     */
    @GET("/v1/models")
    fun validateBearerToken(): Completable

    /**
     * Generates image by prompt.
     *
     * Documentation: https://platform.openai.com/docs/api-reference/images/create
     */
    @POST("/v1/images/generations")
    fun generateImage(@Body request: OpenAiRequest): Single<OpenAiResponse>
}
