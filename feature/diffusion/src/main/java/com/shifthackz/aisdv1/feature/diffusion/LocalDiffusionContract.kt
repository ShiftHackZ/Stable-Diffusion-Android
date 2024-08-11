@file:Suppress("SpellCheckingInspection")

package com.shifthackz.aisdv1.feature.diffusion

internal object LocalDiffusionContract {
    //region LOGGING
    const val TAG = "LocalDiffusion"
    //endregion

    //region MODELS PATHS
    const val UNET_MODEL = "unet/model.ort"
    const val VAE_MODEL = "vae_decoder/model.ort"
    const val TOKENIZER_MODEL = "text_encoder/model.ort"
    const val TOKENIZER_VOCABULARY = "tokenizer/vocab.json"
    const val TOKENIZER_MERGES = "tokenizer/merges.txt"
    //endregion

    //region BETA SCHEDULERS
    const val BETA_SCHEDULER_LINEAR = "linear"
    const val BETA_SCHEDULER_SCALED_LINEAR = "scaled_linear"
    const val BETA_SCHEDULER_SQUARED_v2 = "squaredcos_cap_v2"
    //endregion

    //region PREDICTION TYPES
    const val PREDICTION_EPSILON = "epsilon"
    const val PREDICTION_V = "v_prediction"
    //endregion

    //region SOLVERS
    const val SOLVER_MIDPOINT = "midpoint"
    //endregion

    //region ALGORITHMS
    const val DPM_SOLVER_PP = "dpmsolver++"
    //endregion

    //region KEYS
    const val KEY_INPUT_IDS = "input_ids"
    const val KEY_ENCODER_HIDDEN_STATES = "encoder_hidden_states"
    const val KEY_SAMPLE = "sample"
    const val KEY_TIME_STEP = "timestep"
    const val KEY_LATENT_SAMPLE = "latent_sample"
    //endregion

    //region ORT KEYS
    const val ORT_KEY_MODEL_FORMAT = "session.load_model_format"
    //endregion

    //region ORT_VALUES
    const val ORT = "ORT"
    //endregion
}
