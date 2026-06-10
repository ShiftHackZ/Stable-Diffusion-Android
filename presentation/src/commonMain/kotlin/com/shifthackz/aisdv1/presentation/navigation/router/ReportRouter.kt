package com.shifthackz.aisdv1.presentation.navigation.router

interface ReportRouter {
    fun navigateBack()
}

object NoOpReportRouter : ReportRouter {
    override fun navigateBack() = Unit
}
