package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface BackgroundTaskManager {
    fun scheduleTextToImageTask(payload: TextToImagePayload)
    fun scheduleImageToImageTask(payload: ImageToImagePayload)
}
