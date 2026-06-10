package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Provides the `GenerationResultContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object GenerationResultContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "generation_results"
    /**
     * Exposes the `CREATED_AT_INDEX` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val CREATED_AT_INDEX = "index_generation_results_created_at"

    /**
     * Exposes the `ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ID = "id"
    /**
     * Exposes the `IMAGE_BASE_64` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val IMAGE_BASE_64 = "image_base_64"
    /**
     * Exposes the `ORIGINAL_IMAGE_BASE_64` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val ORIGINAL_IMAGE_BASE_64 = "original_image_base_64"
    /**
     * Exposes the `CREATED_AT` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val CREATED_AT = "created_at"
    /**
     * Exposes the `GENERATION_TYPE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val GENERATION_TYPE = "generation_type"

    /**
     * Exposes the `PROMPT` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val PROMPT = "prompt"
    /**
     * Exposes the `NEGATIVE_PROMPT` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val NEGATIVE_PROMPT = "negative_prompt"
    /**
     * Exposes the `WIDTH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val WIDTH = "width"
    /**
     * Exposes the `HEIGHT` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val HEIGHT = "height"
    /**
     * Exposes the `SAMPLING_STEPS` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS = "sampling_steps"
    /**
     * Exposes the `CFG_SCALE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val CFG_SCALE = "cfg_scale"
    /**
     * Exposes the `RESTORE_FACES` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val RESTORE_FACES = "restore_faces"
    /**
     * Exposes the `SAMPLER` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLER = "sampler"
    /**
     * Exposes the `SEED` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SEED = "seed"
    /**
     * Exposes the `SUB_SEED` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SUB_SEED = "sub_seed"
    /**
     * Exposes the `SUB_SEED_STRENGTH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SUB_SEED_STRENGTH = "sub_seed_strength"
    /**
     * Exposes the `DENOISING_STRENGTH` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val DENOISING_STRENGTH = "denoising_strength"
    /**
     * Exposes the `HIDDEN` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val HIDDEN = "hidden"
}
