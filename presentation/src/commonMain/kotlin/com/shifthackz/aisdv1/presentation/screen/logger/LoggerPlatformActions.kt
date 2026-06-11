package com.shifthackz.aisdv1.presentation.screen.logger

/**
 * Defines the `LoggerPlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface LoggerPlatformActions {
    /**
     * Executes the `copyLogs` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun copyLogs(text: String)

    /**
     * Performs the SDAI side effect handled by `shareLogs`.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun shareLogs(text: String)
}

/**
 * Provides the `NoOpLoggerPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpLoggerPlatformActions : LoggerPlatformActions {
    /**
     * Executes the `copyLogs` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun copyLogs(text: String) = Unit

    /**
     * Performs the SDAI side effect handled by `shareLogs`.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun shareLogs(text: String) = Unit
}
