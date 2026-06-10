@file:Suppress("SpellCheckingInspection")

package com.shifthackz.aisdv1.feature.onnx

/**
 * Provides the `LocalDiffusionContract` singleton used by the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal object LocalDiffusionContract {
    //region LOGGING
    /**
     * Exposes the `TAG` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val TAG = "LocalDiffusion"
    //endregion

    //region MODELS PATHS
    /**
     * Exposes the `UNET_MODEL` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val UNET_MODEL = "unet/model.ort"
    /**
     * Exposes the `VAE_MODEL` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val VAE_MODEL = "vae_decoder/model.ort"
    /**
     * Exposes the `TOKENIZER_MODEL` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val TOKENIZER_MODEL = "text_encoder/model.ort"
    /**
     * Exposes the `TOKENIZER_VOCABULARY` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val TOKENIZER_VOCABULARY = "tokenizer/vocab.json"
    /**
     * Exposes the `TOKENIZER_MERGES` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val TOKENIZER_MERGES = "tokenizer/merges.txt"
    //endregion

    //region BETA SCHEDULERS
    /**
     * Exposes the `BETA_SCHEDULER_LINEAR` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val BETA_SCHEDULER_LINEAR = "linear"
    /**
     * Exposes the `BETA_SCHEDULER_SCALED_LINEAR` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val BETA_SCHEDULER_SCALED_LINEAR = "scaled_linear"
    /**
     * Exposes the `BETA_SCHEDULER_SQUARED_v2` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val BETA_SCHEDULER_SQUARED_v2 = "squaredcos_cap_v2"
    //endregion

    //region PREDICTION TYPES
    /**
     * Exposes the `PREDICTION_EPSILON` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val PREDICTION_EPSILON = "epsilon"
    /**
     * Exposes the `PREDICTION_V` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val PREDICTION_V = "v_prediction"
    //endregion

    //region SOLVERS
    /**
     * Exposes the `SOLVER_MIDPOINT` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val SOLVER_MIDPOINT = "midpoint"
    //endregion

    //region ALGORITHMS
    /**
     * Exposes the `DPM_SOLVER_PP` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val DPM_SOLVER_PP = "dpmsolver++"
    //endregion

    //region KEYS
    /**
     * Exposes the `KEY_INPUT_IDS` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEY_INPUT_IDS = "input_ids"
    /**
     * Exposes the `KEY_ENCODER_HIDDEN_STATES` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEY_ENCODER_HIDDEN_STATES = "encoder_hidden_states"
    /**
     * Exposes the `KEY_SAMPLE` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEY_SAMPLE = "sample"
    /**
     * Exposes the `KEY_TIME_STEP` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEY_TIME_STEP = "timestep"
    /**
     * Exposes the `KEY_LATENT_SAMPLE` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val KEY_LATENT_SAMPLE = "latent_sample"
    //endregion

    //region ORT KEYS
    /**
     * Exposes the `ORT_KEY_MODEL_FORMAT` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val ORT_KEY_MODEL_FORMAT = "session.load_model_format"
    //endregion

    //region ORT_VALUES
    /**
     * Exposes the `ORT` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    const val ORT = "ORT"
    //endregion
}
