package com.shifthackz.aisdv1.presentation.utils

object Constants {
    const val PAGINATION_PAYLOAD_SIZE = 20

    const val PARAM_ITEM_ID = "itemId"

    const val ROUTE_SPLASH = "splash"
    const val ROUTE_HOME = "home"
    const val ROUTE_TXT_TO_IMG = "text_to_image"
    const val ROUTE_IMG_TO_IMG = "image_to_image"
    const val ROUTE_GALLERY = "gallery"
    const val ROUTE_GALLERY_DETAIL = "gallery_detail"
    const val ROUTE_GALLERY_DETAIL_FULL = "$ROUTE_GALLERY_DETAIL/{$PARAM_ITEM_ID}"

    const val SAMPLING_STEPS_RANGE_MIN = 1
    const val SAMPLING_STEPS_RANGE_MAX = 150

    const val CFG_SCALE_RANGE_MIN = 1
    const val CFG_SCALE_RANGE_MAX = 30

    const val MIME_TYPE_ZIP = "application/zip"
    const val MIME_TYPE_JPG = "image/jpeg"
}
