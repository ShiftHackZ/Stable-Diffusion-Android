package com.shifthackz.aisdv1.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread

/**
 * Checks that a downloaded model artifact starts with a ZIP archive signature.
 *
 * iOS keeps downloadable model bundles as `model.zip`, so this catches CDN or host
 * failures that return an HTML/error payload with a successful transfer callback.
 *
 * @receiver Absolute path to the candidate archive.
 * @return `true` when the file begins with a ZIP header.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
internal fun String.hasZipArchiveSignature(): Boolean = memScoped {
    val file = fopen(this@hasZipArchiveSignature, "rb") ?: return@memScoped false
    return try {
        val bytes = allocArray<ByteVar>(ZIP_SIGNATURE_LENGTH)
        val readBytes = fread(bytes, 1uL, ZIP_SIGNATURE_LENGTH.toULong(), file)
        if (readBytes < ZIP_SIGNATURE_LENGTH.toULong()) return@memScoped false

        bytes[0] == ZIP_MAGIC_FIRST &&
            bytes[1] == ZIP_MAGIC_SECOND &&
            zipSignatureSuffixes.any { suffix ->
                bytes[2] == suffix.first && bytes[3] == suffix.second
            }
    } finally {
        fclose(file)
    }
}

private const val ZIP_SIGNATURE_LENGTH = 4
private val ZIP_MAGIC_FIRST = 0x50.toByte()
private val ZIP_MAGIC_SECOND = 0x4B.toByte()
private val zipSignatureSuffixes = listOf(
    0x03.toByte() to 0x04.toByte(),
    0x05.toByte() to 0x06.toByte(),
    0x07.toByte() to 0x08.toByte(),
)
