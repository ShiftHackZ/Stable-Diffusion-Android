package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface BackgroundTaskManager {
    fun scheduleTextToImageTask(payload: TextToImagePayload)
}
