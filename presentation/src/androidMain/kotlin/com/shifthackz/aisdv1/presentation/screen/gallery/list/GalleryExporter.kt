package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemsUseCase
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter

/**
 * Coordinates `GalleryExporter` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class GalleryExporter(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `getGalleryItemsUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getGalleryItemsUseCase: GetGalleryItemsUseCase,
    /**
     * Exposes the `getAllGalleryUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    /**
     * Exposes the `base64ToBitmapConverter` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : GalleryExportService, FileSavableExporter.BmpToFile, FileSavableExporter.FilesToZip {

    /**
     * Executes the `export` step in the SDAI presentation layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `export`.
     * @author Dmitriy Moroz
     */
    override suspend fun export(ids: List<Long>?): GalleryExportResult {
        val chain = ids?.let { getGalleryItemsUseCase(it) } ?: getAllGalleryUseCase()
        return chain
            .map { aiDomain -> aiDomain to Input(aiDomain.image) }
            .map { (aiDomain, input) ->
                aiDomain to base64ToBitmapConverter(input)
            }
            .map(::saveBitmapToFileImpl)
            .let(::saveFilesToZip)
            .let { GalleryExportResult(filePath = it.absolutePath) }
    }

    /**
     * Converts SDAI data with `saveBitmapToFileImpl`.
     *
     * @param data data value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun saveBitmapToFileImpl(data: Pair<AiGenerationResult, Output>) =
        saveBitmapToFile(data.first.hashCode().toString(), data.second.bitmap)
}
