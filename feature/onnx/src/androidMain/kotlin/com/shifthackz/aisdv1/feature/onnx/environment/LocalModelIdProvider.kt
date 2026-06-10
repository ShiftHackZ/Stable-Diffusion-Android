package com.shifthackz.aisdv1.feature.onnx.environment

fun interface LocalModelIdProvider {
    fun get(): String
}
