package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetGalleryItemsUseCase
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter

class GalleryExporter(
    override val fileProviderDescriptor: FileProviderDescriptor,
    private val getGalleryItemsUseCase: GetGalleryItemsUseCase,
    private val getAllGalleryUseCase: GetAllGalleryUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : GalleryExportService, FileSavableExporter.BmpToFile, FileSavableExporter.FilesToZip {

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

    private fun saveBitmapToFileImpl(data: Pair<AiGenerationResult, Output>) =
        saveBitmapToFile(data.first.hashCode().toString(), data.second.bitmap)
}
