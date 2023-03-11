package com.shifthackz.aisdv1.presentation.features

import android.app.Activity
import android.net.Uri
import com.google.android.play.core.review.ReviewManager
import com.shifthackz.aisdv1.core.extensions.openUri

fun interface InAppReviewFeature {
    operator fun invoke(activity: Activity)
}


private const val APP_MARKET_PACKAGE = "com.shifthackz.aisdv1.app"

class InAppGooglePlayReview(
    private val manager: ReviewManager,
) : InAppReviewFeature {

    override fun invoke(activity: Activity) {
        activity.openUri(Uri.parse("market://details?id=$APP_MARKET_PACKAGE"))
        /*val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }*/
    }
}
