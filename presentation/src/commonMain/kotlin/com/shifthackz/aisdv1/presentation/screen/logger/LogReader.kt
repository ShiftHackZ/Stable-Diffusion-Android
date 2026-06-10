package com.shifthackz.aisdv1.presentation.screen.logger

interface LogReader {
    suspend fun read(): String
}

object NoOpLogReader : LogReader {
    override suspend fun read() = ""
}
