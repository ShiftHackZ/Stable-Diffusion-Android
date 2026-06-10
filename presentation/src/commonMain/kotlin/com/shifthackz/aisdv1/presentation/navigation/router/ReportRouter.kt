package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `ReportRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ReportRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
}

/**
 * Provides the `NoOpReportRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpReportRouter : ReportRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
}
