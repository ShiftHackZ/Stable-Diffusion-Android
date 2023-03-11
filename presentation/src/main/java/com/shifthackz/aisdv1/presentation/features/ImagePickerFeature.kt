package com.shifthackz.aisdv1.presentation.features

import android.content.Context
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shz.imagepicker.imagepicker.ImagePicker
import com.shz.imagepicker.imagepicker.ImagePickerCallback
import com.shz.imagepicker.imagepicker.model.GalleryPicker

interface ImagePickerFeature {

    val fileProviderDescriptor: FileProviderDescriptor

    fun pickPhoto(context: Context, callback: ImagePickerCallback) = ImagePicker
        .Builder(fileProviderDescriptor.providerPath, callback)
        .useGallery(true)
        .useCamera(false)
        .autoRotate(true)
        .multipleSelection(false)
        .galleryPicker(GalleryPicker.NATIVE)
        .build()
        .launch(context)

    fun takePhoto(context: Context, callback: ImagePickerCallback) = ImagePicker
        .Builder(fileProviderDescriptor.providerPath, callback)
        .useGallery(false)
        .useCamera(true)
        .build()
        .launch(context)
}
