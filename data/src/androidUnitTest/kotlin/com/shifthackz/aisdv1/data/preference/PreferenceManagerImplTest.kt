package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PreferenceManagerImplTest {

    private lateinit var keyValueStore: InMemoryKeyValueStore
    private lateinit var preferenceManager: PreferenceManagerImpl

    @Before
    fun initialize() {
        keyValueStore = InMemoryKeyValueStore()
        preferenceManager = PreferenceManagerImpl(keyValueStore)
    }

    @Test
    fun `given user reads default serverUrl, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        Assert.assertEquals("", preferenceManager.automatic1111ServerUrl)

        preferenceManager.automatic1111ServerUrl = "https://192.168.0.1:7860//"

        Assert.assertEquals("https://192.168.0.1:7860", preferenceManager.automatic1111ServerUrl)
        Assert.assertEquals("https://192.168.0.1:7860", preferenceManager.observe().first().serverUrl)
    }

    @Test
    fun `given user reads default demoMode, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        Assert.assertFalse(preferenceManager.demoMode)

        preferenceManager.demoMode = true

        Assert.assertTrue(preferenceManager.demoMode)
        Assert.assertTrue(preferenceManager.observe().first().demoMode)
    }

    @Test
    fun `given user reads default monitorConnectivity, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        preferenceManager.source = ServerSource.AUTOMATIC1111
        Assert.assertFalse(preferenceManager.monitorConnectivity)

        preferenceManager.monitorConnectivity = true

        Assert.assertTrue(preferenceManager.monitorConnectivity)
        Assert.assertTrue(preferenceManager.observe().first().monitorConnectivity)
    }

    @Test
    fun `given source without own server tag, monitorConnectivity remains false`() = runTest {
        preferenceManager.source = ServerSource.HORDE
        preferenceManager.monitorConnectivity = true

        Assert.assertFalse(preferenceManager.monitorConnectivity)
        Assert.assertFalse(preferenceManager.observe().first().monitorConnectivity)
    }

    @Test
    fun `given user reads autoSaveAiResults, changes it, expected changed value, observer emits changed value`() = runTest {
        preferenceManager.autoSaveAiResults = false
        Assert.assertFalse(preferenceManager.autoSaveAiResults)

        preferenceManager.autoSaveAiResults = true

        Assert.assertTrue(preferenceManager.autoSaveAiResults)
        Assert.assertTrue(preferenceManager.observe().first().autoSaveAiResults)
    }

    @Test
    fun `given user reads saveToMediaStore, changes it, expected changed value, observer emits changed value`() = runTest {
        preferenceManager.saveToMediaStore = false
        Assert.assertFalse(preferenceManager.saveToMediaStore)

        preferenceManager.saveToMediaStore = true

        Assert.assertTrue(preferenceManager.saveToMediaStore)
        Assert.assertTrue(preferenceManager.observe().first().saveToMediaStore)
    }

    @Test
    fun `given user changes form options, expected changed values and observer emits changed values`() = runTest {
        preferenceManager.formAdvancedOptionsAlwaysShow = true
        preferenceManager.formPromptTaggedInput = true

        val settings = preferenceManager.observe().first()

        Assert.assertTrue(preferenceManager.formAdvancedOptionsAlwaysShow)
        Assert.assertTrue(preferenceManager.formPromptTaggedInput)
        Assert.assertTrue(settings.formAdvancedOptionsAlwaysShow)
        Assert.assertTrue(settings.formPromptTaggedInput)
    }

    @Test
    fun `given user reads default source, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        Assert.assertEquals(ServerSource.AUTOMATIC1111, preferenceManager.source)

        preferenceManager.source = ServerSource.LOCAL_MICROSOFT_ONNX

        Assert.assertEquals(ServerSource.LOCAL_MICROSOFT_ONNX, preferenceManager.source)
        Assert.assertEquals(ServerSource.LOCAL_MICROSOFT_ONNX, preferenceManager.observe().first().source)
    }

    @Test
    fun `given user reads default sdModel, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        Assert.assertEquals("", preferenceManager.sdModel)

        preferenceManager.sdModel = "model1504"

        Assert.assertEquals("model1504", preferenceManager.sdModel)
        Assert.assertEquals("model1504", preferenceManager.observe().first().sdModel)
    }

    @Test
    fun `given user reads default hordeApiKey, changes it, expected default value, then changed value, observer emits changed value`() = runTest {
        Assert.assertEquals("", preferenceManager.hordeApiKey)

        preferenceManager.hordeApiKey = "key"

        Assert.assertEquals("key", preferenceManager.hordeApiKey)
        Assert.assertEquals("key", preferenceManager.observe().first().hordeApiKey)
    }

    @Test
    fun `given user reads and changes api keys, expected changed values`() {
        preferenceManager.openAiApiKey = "open-ai-key"
        preferenceManager.huggingFaceApiKey = "hugging-face-key"
        preferenceManager.stabilityAiApiKey = "stability-ai-key"
        preferenceManager.stabilityAiEngineId = "engine-id"

        Assert.assertEquals("open-ai-key", preferenceManager.openAiApiKey)
        Assert.assertEquals("hugging-face-key", preferenceManager.huggingFaceApiKey)
        Assert.assertEquals("stability-ai-key", preferenceManager.stabilityAiApiKey)
        Assert.assertEquals("engine-id", preferenceManager.stabilityAiEngineId)
    }

    @Test
    fun `given user reads default huggingFaceModel, changes it, expected default value, then changed value`() {
        Assert.assertEquals(HuggingFaceModel.default.alias, preferenceManager.huggingFaceModel)

        preferenceManager.huggingFaceModel = "key"

        Assert.assertEquals("key", preferenceManager.huggingFaceModel)
    }

    @Test
    fun `given user changes setup and local model values, expected changed values`() {
        Assert.assertTrue(preferenceManager.forceSetupAfterUpdate)

        preferenceManager.forceSetupAfterUpdate = false
        preferenceManager.localOnnxModelId = "onnx-key"
        preferenceManager.localMediaPipeModelId = "mediapipe-key"
        preferenceManager.localBonsaiModelId = "bonsai-key"
        preferenceManager.localBonsaiCustomModelPath = "/models/bonsai"
        preferenceManager.localOnnxUseNNAPI = true
        preferenceManager.localOnnxAllowCancel = true
        preferenceManager.localOnnxSchedulerThread = SchedulersToken.IO_THREAD

        Assert.assertFalse(preferenceManager.forceSetupAfterUpdate)
        Assert.assertEquals("onnx-key", preferenceManager.localOnnxModelId)
        Assert.assertEquals("mediapipe-key", preferenceManager.localMediaPipeModelId)
        Assert.assertEquals("bonsai-key", preferenceManager.localBonsaiModelId)
        Assert.assertEquals("/models/bonsai", preferenceManager.localBonsaiCustomModelPath)
        Assert.assertTrue(preferenceManager.localOnnxUseNNAPI)
        Assert.assertTrue(preferenceManager.localOnnxAllowCancel)
        Assert.assertEquals(SchedulersToken.IO_THREAD, preferenceManager.localOnnxSchedulerThread)
    }

    @Test
    fun `given user changes local NNAPI, expected observer emits changed value`() = runTest {
        preferenceManager.localOnnxUseNNAPI = true

        Assert.assertTrue(preferenceManager.observe().first().localUseNNAPI)
    }

    @Test
    fun `given user changes design options, expected changed values and observer emits changed values`() = runTest {
        preferenceManager.designUseSystemColorPalette = true
        preferenceManager.designUseSystemDarkTheme = false
        preferenceManager.designDarkTheme = false
        preferenceManager.designColorToken = "${ColorToken.PEACH}"
        preferenceManager.designDarkThemeToken = "${DarkThemeToken.MOCHA}"

        val settings = preferenceManager.observe().first()

        Assert.assertTrue(preferenceManager.designUseSystemColorPalette)
        Assert.assertFalse(preferenceManager.designUseSystemDarkTheme)
        Assert.assertFalse(preferenceManager.designDarkTheme)
        Assert.assertEquals("${ColorToken.PEACH}", preferenceManager.designColorToken)
        Assert.assertEquals("${DarkThemeToken.MOCHA}", preferenceManager.designDarkThemeToken)
        Assert.assertTrue(settings.designUseSystemColorPalette)
        Assert.assertFalse(settings.designUseSystemDarkTheme)
        Assert.assertFalse(settings.designDarkTheme)
        Assert.assertEquals("${ColorToken.PEACH}", settings.designColorToken)
        Assert.assertEquals("${DarkThemeToken.MOCHA}", settings.designDarkThemeToken)
    }

    @Test
    fun `given user changes background and gallery values, expected changed values and observer emits changed values`() = runTest {
        preferenceManager.backgroundGeneration = true
        preferenceManager.backgroundProcessCount = 3
        preferenceManager.galleryGrid = Grid.Fixed3

        val settings = preferenceManager.observe().first()

        Assert.assertTrue(preferenceManager.backgroundGeneration)
        Assert.assertEquals(3, preferenceManager.backgroundProcessCount)
        Assert.assertEquals(Grid.Fixed3, preferenceManager.galleryGrid)
        Assert.assertTrue(settings.backgroundGeneration)
        Assert.assertEquals(Grid.Fixed3, settings.galleryGrid)
    }

    @Test
    fun `given user changes language, expected changed value and observer emits changed value`() = runTest {
        preferenceManager.languageCode = "uk"

        val settings = preferenceManager.observe().first()

        Assert.assertEquals("uk", preferenceManager.languageCode)
        Assert.assertEquals("uk", settings.languageCode)
    }

    private class InMemoryKeyValueStore : KeyValueStore {
        private val strings = mutableMapOf<String, String>()
        private val booleans = mutableMapOf<String, Boolean>()
        private val ints = mutableMapOf<String, Int>()

        override fun getString(key: String, default: String): String = strings[key] ?: default

        override fun putString(key: String, value: String) {
            strings[key] = value
        }

        override fun getBoolean(key: String, default: Boolean): Boolean = booleans[key] ?: default

        override fun putBoolean(key: String, value: Boolean) {
            booleans[key] = value
        }

        override fun getInt(key: String, default: Int): Int = ints[key] ?: default

        override fun putInt(key: String, value: Int) {
            ints[key] = value
        }
    }
}
