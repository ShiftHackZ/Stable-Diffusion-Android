package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

internal fun ImageToImagePayload.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    objectOutputStream.close()
    return byteArrayOutputStream.toByteArray()
}

internal fun ByteArray.toImageToImagePayload(): ImageToImagePayload? {
    val byteArrayInputStream = ByteArrayInputStream(this)
    val objectInputStream = ObjectInputStream(byteArrayInputStream)
    return objectInputStream.readObject() as? ImageToImagePayload
}
