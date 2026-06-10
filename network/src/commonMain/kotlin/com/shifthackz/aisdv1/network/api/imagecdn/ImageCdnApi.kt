package com.shifthackz.aisdv1.network.api.imagecdn

/**
 * Defines the `ImageCdnApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface ImageCdnApi {

    /**
     * Loads SDAI data through `fetchRandomImageBytes`.
     *
     * @return Result produced by `fetchRandomImageBytes`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchRandomImageBytes(): ByteArray
}
