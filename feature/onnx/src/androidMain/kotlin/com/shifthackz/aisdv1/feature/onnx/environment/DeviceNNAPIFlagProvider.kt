package com.shifthackz.aisdv1.feature.onnx.environment

fun interface DeviceNNAPIFlagProvider {
    fun get(): Int
}
