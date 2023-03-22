package com.shifthackz.aisdv1.domain.feature.billing

import android.app.Activity
import android.content.Context

interface BillingFeature {
    fun initialize(context: Context)
    fun launchPurchase(activity: Activity, type: BillingType)
}
