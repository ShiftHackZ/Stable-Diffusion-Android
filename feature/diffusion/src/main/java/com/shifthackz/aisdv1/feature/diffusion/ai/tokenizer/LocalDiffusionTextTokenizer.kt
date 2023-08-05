package com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer

import ai.onnxruntime.OnnxTensor

interface LocalDiffusionTextTokenizer {
    val maxLength: Int
    fun initialize()
    fun decode(ids: IntArray?): String?
    fun encode(text: String?): IntArray?
    fun tensor(ids: IntArray?): OnnxTensor?
    fun createUnconditionalInput(text: String?): IntArray?
    fun close()
}
