package com.shifthackz.aisdv1.presentation.screen.gallery.list

data class GalleryExportResult(
    val filePath: String,
)

interface GalleryExportService {
    suspend fun export(ids: List<Long>? = null): GalleryExportResult
}

object NoOpGalleryExportService : GalleryExportService {
    override suspend fun export(ids: List<Long>?): GalleryExportResult =
        error("Gallery export is not available on this platform yet.")
}
