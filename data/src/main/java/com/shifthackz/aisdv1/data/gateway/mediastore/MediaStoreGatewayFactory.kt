package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.Context
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway

internal class MediaStoreGatewayFactory(
    private val context: Context,
    private val fileProviderDescriptor: FileProviderDescriptor,
) {

    operator fun invoke(): MediaStoreGateway {
        if (shouldUseNewMediaStore()) {
            debugLog("Using Tiramisu and higher implementation for MediaStore")
            return MediaStoreGatewayImpl(context, fileProviderDescriptor)
        }
        debugLog("Using deprecated implementation for MediaStore")
        return MediaStoreGatewayOldImpl()
    }
}
