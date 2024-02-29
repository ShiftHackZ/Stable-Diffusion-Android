package com.shifthackz.aisdv1.network.api.imagecdn

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.rxjava3.core.Single
import kotlin.random.Random

internal class ImageCdnRestApiImpl(private val rawApi: ImageCdnRestApi.RawApi) : ImageCdnRestApi {

    override fun fetchRandomImage(): Single<Bitmap> = Random
        .nextInt(MIN, MAX + 1)
        .let(Int::toString)
        .let(rawApi::fetchRandomImage)
        .map { body ->
            val bytes = body.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    companion object {
        private const val MIN = 400
        private const val MAX = 700
    }
}
