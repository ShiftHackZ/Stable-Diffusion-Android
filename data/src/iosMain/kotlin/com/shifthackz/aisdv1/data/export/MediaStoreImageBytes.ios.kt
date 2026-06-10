package com.shifthackz.aisdv1.data.export

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Converts SDAI data with `toMediaStoreImageBytes`.
 *
 * @return Result produced by `toMediaStoreImageBytes`.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalEncodingApi::class)
internal actual fun String.toMediaStoreImageBytes(): ByteArray =
    substringAfter("base64,", this)
        .filterNot(Char::isWhitespace)
        .let(Base64.Default::decode)
