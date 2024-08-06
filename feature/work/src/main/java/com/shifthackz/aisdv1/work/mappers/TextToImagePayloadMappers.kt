package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

internal fun TextToImagePayload.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    objectOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}

internal fun ByteArray.toTextToImagePayload(): TextToImagePayload? {
    val byteArrayInputStream = ByteArrayInputStream(this)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject() as? TextToImagePayload
}
