package com.shifthackz.aisdv1.presentation.features

import android.app.Activity
import com.google.android.play.core.review.ReviewManager

fun interface InAppReviewFeature {
    operator fun invoke(activity: Activity)
}

class InAppGooglePlayReview(
    private val manager: ReviewManager,
) : InAppReviewFeature {

    override fun invoke(activity: Activity) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}
