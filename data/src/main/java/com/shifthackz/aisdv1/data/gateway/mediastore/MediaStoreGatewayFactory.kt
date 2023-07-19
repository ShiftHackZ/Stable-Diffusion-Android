package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.Context
import android.os.Build
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway

internal class MediaStoreGatewayFactory(
    private val context: Context,
    private val fileProviderDescriptor: FileProviderDescriptor,
) {

    operator fun invoke(): MediaStoreGateway {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return MediaStoreGatewayImpl(context, fileProviderDescriptor)
        }
        return MediaStoreGatewayOldImpl()
    }
}
