package com.shifthackz.aisdv1.network.extensions

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import java.io.File

fun <T : Any> ResponseBody.saveFile(
    path: String,
    stateProgress: (Int) -> T,
    stateComplete: (File) -> T,
    stateFailed: (Throwable) -> T,
): Observable<T> = Observable.create { emitter ->
    val emit: (value: T) -> Unit = {
        if (!emitter.isDisposed) it.let(emitter::onNext)
    }
    val file = File(path)
    emit(stateProgress(0))
    try {
        byteStream().use { inputStream ->
            file.outputStream().use { outputStream ->
                val totalBytes = contentLength()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var progressBytes = 0L
                var bytes = inputStream.read(buffer)
                while (bytes >= 0) {
                    outputStream.write(buffer, 0, bytes)
                    progressBytes += bytes
                    bytes = inputStream.read(buffer)
                    emit(stateProgress(((progressBytes * 100) / totalBytes).toInt()))
                }
            }
        }
        emit(stateComplete(file))
    } catch (e: Exception) {
        emit(stateFailed(e))
    }
}
