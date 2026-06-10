package com.shifthackz.aisdv1.data.export

/**
 * Converts SDAI data with `toMediaStoreImageBytes`.
 *
 * @return Result produced by `toMediaStoreImageBytes`.
 * @author Dmitriy Moroz
 */
internal expect fun String.toMediaStoreImageBytes(): ByteArray
