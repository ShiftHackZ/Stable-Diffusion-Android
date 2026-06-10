package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.ConfigurationStore

internal class KeyValueConfigurationStore(
    private val keyValueStore: KeyValueStore,
) : ConfigurationStore {

    override var automatic1111ServerUrl: String
        get() = keyValueStore.getString(KEY_SERVER_URL).fixUrlSlashes()
        set(value) = keyValueStore.putString(KEY_SERVER_URL, value.fixUrlSlashes())

    override var swarmUiServerUrl: String
        get() = keyValueStore.getString(KEY_SWARM_SERVER_URL).fixUrlSlashes()
        set(value) = keyValueStore.putString(KEY_SWARM_SERVER_URL, value.fixUrlSlashes())

    override var swarmUiModel: String
        get() = keyValueStore.getString(KEY_SWARM_MODEL)
        set(value) = keyValueStore.putString(KEY_SWARM_MODEL, value)

    override var demoMode: Boolean
        get() = keyValueStore.getBoolean(KEY_DEMO_MODE)
        set(value) = keyValueStore.putBoolean(KEY_DEMO_MODE, value)

    override var source: ServerSource
        get() = ServerSource.parse(keyValueStore.getString(KEY_SERVER_SOURCE, ServerSource.AUTOMATIC1111.key))
        set(value) = keyValueStore.putString(KEY_SERVER_SOURCE, value.key)

    override var hordeApiKey: String
        get() = keyValueStore.getString(KEY_HORDE_API_KEY)
        set(value) = keyValueStore.putString(KEY_HORDE_API_KEY, value)

    override var openAiApiKey: String
        get() = keyValueStore.getString(KEY_OPEN_AI_API_KEY)
        set(value) = keyValueStore.putString(KEY_OPEN_AI_API_KEY, value)

    override var huggingFaceApiKey: String
        get() = keyValueStore.getString(KEY_HUGGING_FACE_API_KEY)
        set(value) = keyValueStore.putString(KEY_HUGGING_FACE_API_KEY, value)

    override var huggingFaceModel: String
        get() = keyValueStore.getString(KEY_HUGGING_FACE_MODEL_KEY, HuggingFaceModel.default.alias)
        set(value) = keyValueStore.putString(KEY_HUGGING_FACE_MODEL_KEY, value)

    override var stabilityAiApiKey: String
        get() = keyValueStore.getString(KEY_STABILITY_AI_API_KEY)
        set(value) = keyValueStore.putString(KEY_STABILITY_AI_API_KEY, value)

    override var stabilityAiEngineId: String
        get() = keyValueStore.getString(KEY_STABILITY_AI_ENGINE_ID_KEY)
        set(value) = keyValueStore.putString(KEY_STABILITY_AI_ENGINE_ID_KEY, value)

    override var localOnnxModelId: String
        get() = keyValueStore.getString(KEY_LOCAL_MODEL_ID)
        set(value) = keyValueStore.putString(KEY_LOCAL_MODEL_ID, value)

    override var localOnnxModelPath: String
        get() = keyValueStore.getString(KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH)
        set(value) = keyValueStore.putString(KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH, value)

    override var localMediaPipeModelId: String
        get() = keyValueStore.getString(KEY_MEDIA_PIPE_MODEL_ID)
        set(value) = keyValueStore.putString(KEY_MEDIA_PIPE_MODEL_ID, value)

    override var localMediaPipeModelPath: String
        get() = keyValueStore.getString(KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH)
        set(value) = keyValueStore.putString(KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH, value)

    private fun String.fixUrlSlashes(): String {
        if (isEmpty()) return this
        return replace("://", URL_PROTOCOL_MARKER)
            .replace("//", "/")
            .replace(URL_PROTOCOL_MARKER, "://")
            .trimEnd('/')
    }

    private companion object {
        const val KEY_SERVER_URL = "key_server_url"
        const val KEY_SWARM_SERVER_URL = "key_swarm_server_url"
        const val KEY_SWARM_MODEL = "key_swarm_model"
        const val KEY_DEMO_MODE = "key_demo_mode"
        const val KEY_LOCAL_DIFFUSION_CUSTOM_MODEL_PATH = "key_local_diffusion_custom_model_path"
        const val KEY_MEDIA_PIPE_CUSTOM_MODEL_PATH = "key_mediapipe_custom_model_path"
        const val KEY_SERVER_SOURCE = "key_server_source"
        const val KEY_HORDE_API_KEY = "key_horde_api_key"
        const val KEY_OPEN_AI_API_KEY = "key_open_ai_api_key"
        const val KEY_HUGGING_FACE_API_KEY = "key_hugging_face_api_key"
        const val KEY_HUGGING_FACE_MODEL_KEY = "key_hugging_face_model_key"
        const val KEY_STABILITY_AI_API_KEY = "key_stability_ai_api_key"
        const val KEY_STABILITY_AI_ENGINE_ID_KEY = "key_stability_ai_engine_id_key"
        const val KEY_MEDIA_PIPE_MODEL_ID = "key_mediapipe_model_id"
        const val KEY_LOCAL_MODEL_ID = "key_local_model_id"
        const val URL_PROTOCOL_MARKER = "SDAI_URL_PROTOCOL_MARKER"
    }
}
