@file:Suppress("DEPRECATION")

package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.Context
import com.shifthackz.aisdv1.core.common.extensions.shouldUseNewMediaStore as defaultShouldUseNewMediaStore
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway

/**
 * Coordinates `MediaStoreGatewayFactory` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class MediaStoreGatewayFactory(
    /**
     * Exposes the `context` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `shouldUseNewMediaStore` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val shouldUseNewMediaStore: () -> Boolean = ::defaultShouldUseNewMediaStore,
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
