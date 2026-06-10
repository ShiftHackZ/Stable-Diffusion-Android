package com.shifthackz.aisdv1.presentation.screen.logger

/**
 * Defines the `LogReader` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface LogReader {
    /**
     * Loads SDAI data through `read`.
     *
     * @return Result produced by `read`.
     * @author Dmitriy Moroz
     */
    suspend fun read(): String
}

/**
 * Provides the `NoOpLogReader` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpLogReader : LogReader {
    /**
     * Loads SDAI data through `read`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun read() = ""
}
