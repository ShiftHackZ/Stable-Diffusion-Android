package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract

/**
 * Carries `GenerationResultEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(
    tableName = GenerationResultContract.TABLE,
    indices = [
        Index(
            value = [GenerationResultContract.CREATED_AT],
            name = GenerationResultContract.CREATED_AT_INDEX,
        ),
    ],
)
data class GenerationResultEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = GenerationResultContract.ID)
    val id: Long,
    /**
     * Exposes the `imageBase64` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.IMAGE_BASE_64)
    val imageBase64: String,
    /**
     * Exposes the `originalImageBase64` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.ORIGINAL_IMAGE_BASE_64)
    val originalImageBase64: String,
    /**
     * Exposes the `createdAt` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.CREATED_AT)
    val createdAt: Long,
    /**
     * Exposes the `generationType` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.GENERATION_TYPE)
    val generationType: String,
    /**
     * Exposes the `prompt` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.PROMPT)
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.NEGATIVE_PROMPT)
    val negativePrompt: String,
    /**
     * Exposes the `width` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.WIDTH)
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.HEIGHT)
    val height: Int,
    /**
     * Exposes the `samplingSteps` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.SAMPLING_STEPS)
    val samplingSteps: Int,
    /**
     * Exposes the `cfgScale` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.CFG_SCALE)
    val cfgScale: Float,
    /**
     * Exposes the `restoreFaces` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.RESTORE_FACES)
    val restoreFaces: Boolean,
    /**
     * Exposes the `sampler` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.SAMPLER)
    val sampler: String,
    /**
     * Exposes the `seed` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.SEED)
    val seed: String,
    /**
     * Exposes the `subSeed` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.SUB_SEED, defaultValue = "")
    val subSeed: String,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.SUB_SEED_STRENGTH, defaultValue = "${0f}")
    val subSeedStrength: Float,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.DENOISING_STRENGTH, defaultValue = "${0f}")
    val denoisingStrength: Float,
    /**
     * Exposes the `hidden` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.HIDDEN, defaultValue = "0")
    val hidden: Boolean,
)
