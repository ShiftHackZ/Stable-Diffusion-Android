package com.shifthackz.aisdv1.presentation.screen.gallery.list

/**
 * Carries `GalleryExportResult` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class GalleryExportResult(
    /**
     * Exposes the `filePath` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val filePath: String,
)

/**
 * Defines the `GalleryExportService` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GalleryExportService {
    /**
     * Executes the `export` step in the SDAI presentation layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `export`.
     * @author Dmitriy Moroz
     */
    suspend fun export(ids: List<Long>? = null): GalleryExportResult
}

/**
 * Provides the `NoOpGalleryExportService` singleton used by the SDAI presentation layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpGalleryExportService : GalleryExportService {
    /**
     * Executes the `export` step in the SDAI presentation layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `export`.
     * @author Dmitriy Moroz
     */
    override suspend fun export(ids: List<Long>?): GalleryExportResult =
        error("Gallery export is not available on this platform yet.")
}
