package com.shifthackz.aisdv1.storage.db.persistent.contract

internal object GenerationResultContract {
    const val TABLE = "generation_results"

    const val ID = "id"
    const val IMAGE_BASE_64 = "image_base_64"
    const val ORIGINAL_IMAGE_BASE_64 = "original_image_base_64"
    const val CREATED_AT = "created_at"
    const val GENERATION_TYPE = "generation_type"

    const val PROMPT = "prompt"
    const val NEGATIVE_PROMPT = "negative_prompt"
    const val WIDTH = "width"
    const val HEIGHT = "height"
    const val SAMPLING_STEPS = "sampling_steps"
    const val CFG_SCALE = "cfg_scale"
    const val RESTORE_FACES = "restore_faces"
    const val SAMPLER = "sampler"
    const val SEED = "seed"
    const val SUB_SEED = "sub_seed"
    const val SUB_SEED_STRENGTH = "sub_seed_strength"
    const val DENOISING_STRENGTH = "denoising_strength"
    const val HIDDEN = "hidden"
}
