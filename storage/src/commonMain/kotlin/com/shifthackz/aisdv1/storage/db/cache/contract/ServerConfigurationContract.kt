package com.shifthackz.aisdv1.storage.db.cache.contract

/**
 * Provides the `ServerConfigurationContract` singleton used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
internal object ServerConfigurationContract {
    /**
     * Exposes the `TABLE` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "server_config"

    /**
     * Exposes the `SERVER_ID` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SERVER_ID = "server_id"
    /**
     * Exposes the `SD_MODEL_CHECKPOINT` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    const val SD_MODEL_CHECKPOINT = "sd_model_checkpoint"
}
