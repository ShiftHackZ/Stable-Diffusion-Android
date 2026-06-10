package com.shifthackz.aisdv1.feature.onnx.ai.tokenizer

import ai.onnxruntime.OnnxTensor

/**
 * Defines the `LocalDiffusionTextTokenizer` contract for the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
interface LocalDiffusionTextTokenizer {
    /**
     * Exposes the `maxLength` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val maxLength: Int
    /**
     * Executes the `initialize` step in the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun initialize()
    /**
     * Executes the `decode` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `decode`.
     * @author Dmitriy Moroz
     */
    fun decode(ids: IntArray?): String?
    /**
     * Executes the `encode` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `encode`.
     * @author Dmitriy Moroz
     */
    fun encode(text: String?): IntArray?
    /**
     * Executes the `tensor` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `tensor`.
     * @author Dmitriy Moroz
     */
    fun tensor(ids: IntArray?): OnnxTensor?
    /**
     * Creates the SDAI value produced by `createUnconditionalInput`.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `createUnconditionalInput`.
     * @author Dmitriy Moroz
     */
    fun createUnconditionalInput(text: String?): IntArray?
    /**
     * Executes the `close` step in the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun close()
}
