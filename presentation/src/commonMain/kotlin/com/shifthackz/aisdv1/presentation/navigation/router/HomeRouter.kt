package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `HomeRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface HomeRouter {
    /**
     * Executes the `navigateToServerSetup` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToServerSetup()
    /**
     * Executes the `navigateToTextToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToTextToImage()
    /**
     * Executes the `navigateToImageToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToImageToImage()
    /**
     * Executes the `navigateToGallery` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToGallery()
    /**
     * Executes the `navigateToSettings` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToSettings()
    /**
     * Executes the `navigateToHistory` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToHistory()
}
