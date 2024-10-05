package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_AI_AUTO_SAVE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DEMO_MODE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_COLOR_TOKEN
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DARK_THEME
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DARK_TOKEN
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_DYNAMIC_COLORS
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DESIGN_SYSTEM_DARK_THEME
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORCE_SETUP_AFTER_UPDATE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_PROMPT_TAGGED_INPUT
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HORDE_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HUGGING_FACE_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_HUGGING_FACE_MODEL_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_MODEL_ID
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_LOCAL_NN_API
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_MONITOR_CONNECTIVITY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_OPEN_AI_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SAVE_TO_MEDIA_STORE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SD_MODEL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_SOURCE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_URL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_STABILITY_AI_API_KEY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_STABILITY_AI_ENGINE_ID_KEY
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PreferenceManagerImplTest {

    private val stubEditor = mock<SharedPreferences.Editor>()
    private val stubPreference = mock<SharedPreferences>()

    private lateinit var preferenceManager : PreferenceManagerImpl

    @Before
    fun initialize() {
        doNothing()
            .whenever(stubEditor)
            .apply()

        whenever(stubEditor.putString(any(), any()))
            .thenReturn(stubEditor)

        whenever(stubEditor.putBoolean(any(), any()))
            .thenReturn(stubEditor)

        whenever(stubPreference.edit())
            .thenReturn(stubEditor)

        whenever(stubPreference.getString(any(), any()))
            .thenReturn("")

        whenever(stubPreference.getBoolean(any(), any()))
            .thenReturn(false)

        preferenceManager = PreferenceManagerImpl(stubPreference)
    }

    @Test
    fun `given user reads default serverUrl, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_SERVER_URL), any()))
            .thenReturn("")

        Assert.assertEquals("", preferenceManager.automatic1111ServerUrl)

        whenever(stubPreference.getString(eq(KEY_SERVER_URL), any()))
            .thenReturn("https://192.168.0.1:7860")

        preferenceManager.automatic1111ServerUrl = "https://192.168.0.1:7860"

        Assert.assertEquals("https://192.168.0.1:7860", preferenceManager.automatic1111ServerUrl)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings ->
                settings.serverUrl == "https://192.168.0.1:7860"
            }
    }

    @Test
    fun `given user reads default demoMode, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_DEMO_MODE), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.demoMode)

        whenever(stubPreference.getBoolean(eq(KEY_DEMO_MODE), any()))
            .thenReturn(true)

        preferenceManager.demoMode = true

        Assert.assertEquals(true, preferenceManager.demoMode)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.demoMode }
    }

    @Test
    fun `given user reads default monitorConnectivity, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_MONITOR_CONNECTIVITY), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.monitorConnectivity)

        whenever(stubPreference.getBoolean(eq(KEY_MONITOR_CONNECTIVITY), any()))
            .thenReturn(true)

        preferenceManager.monitorConnectivity = true

        Assert.assertEquals(true, preferenceManager.monitorConnectivity)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.monitorConnectivity }
    }

    @Test
    fun `given user reads default autoSaveAiResults, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_AI_AUTO_SAVE), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.autoSaveAiResults)

        whenever(stubPreference.getBoolean(eq(KEY_AI_AUTO_SAVE), any()))
            .thenReturn(true)

        preferenceManager.autoSaveAiResults = true

        Assert.assertEquals(true, preferenceManager.autoSaveAiResults)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.autoSaveAiResults }
    }

    @Test
    fun `given user reads default saveToMediaStore, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_SAVE_TO_MEDIA_STORE), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.saveToMediaStore)

        whenever(stubPreference.getBoolean(eq(KEY_SAVE_TO_MEDIA_STORE), any()))
            .thenReturn(true)

        preferenceManager.saveToMediaStore = true

        Assert.assertEquals(true, preferenceManager.saveToMediaStore)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.saveToMediaStore }
    }

    @Test
    fun `given user reads default formAdvancedOptionsAlwaysShow, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.formAdvancedOptionsAlwaysShow)

        whenever(stubPreference.getBoolean(eq(KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS), any()))
            .thenReturn(true)

        preferenceManager.formAdvancedOptionsAlwaysShow = true

        Assert.assertEquals(true, preferenceManager.formAdvancedOptionsAlwaysShow)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.formAdvancedOptionsAlwaysShow }
    }

    @Test
    fun `given user reads default formPromptTaggedInput, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_FORM_PROMPT_TAGGED_INPUT), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.formPromptTaggedInput)

        whenever(stubPreference.getBoolean(eq(KEY_FORM_PROMPT_TAGGED_INPUT), any()))
            .thenReturn(true)

        preferenceManager.formPromptTaggedInput = true

        Assert.assertEquals(true, preferenceManager.formPromptTaggedInput)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.formPromptTaggedInput }
    }

    @Test
    fun `given user reads default source, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_SERVER_SOURCE), any()))
            .thenReturn(ServerSource.AUTOMATIC1111.key)

        Assert.assertEquals(ServerSource.AUTOMATIC1111, preferenceManager.source)

        whenever(stubPreference.getString(eq(KEY_SERVER_SOURCE), any()))
            .thenReturn(ServerSource.LOCAL_MICROSOFT_ONNX.key)

        preferenceManager.source =  ServerSource.LOCAL_MICROSOFT_ONNX

        Assert.assertEquals(ServerSource.LOCAL_MICROSOFT_ONNX, preferenceManager.source)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.source == ServerSource.LOCAL_MICROSOFT_ONNX }
    }

    @Test
    fun `given user reads default sdModel, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_SD_MODEL), any()))
            .thenReturn("model5598")

        Assert.assertEquals("model5598", preferenceManager.sdModel)

        whenever(stubPreference.getString(eq(KEY_SD_MODEL), any()))
            .thenReturn("model1504")

        preferenceManager.sdModel = "model1504"

        Assert.assertEquals("model1504", preferenceManager.sdModel)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.sdModel == "model1504" }
    }

    @Test
    fun `given user reads default hordeApiKey, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_HORDE_API_KEY), any()))
            .thenReturn("00000000")

        Assert.assertEquals("00000000", preferenceManager.hordeApiKey)

        whenever(stubPreference.getString(eq(KEY_HORDE_API_KEY), any()))
            .thenReturn("key")

        preferenceManager.hordeApiKey = "key"

        Assert.assertEquals("key", preferenceManager.hordeApiKey)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.hordeApiKey == "key" }
    }

    @Test
    fun `given user reads default openAiApiKey, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_OPEN_AI_API_KEY), any()))
            .thenReturn("00000000")

        Assert.assertEquals("00000000", preferenceManager.openAiApiKey)

        whenever(stubPreference.getString(eq(KEY_OPEN_AI_API_KEY), any()))
            .thenReturn("key")

        preferenceManager.openAiApiKey = "key"

        Assert.assertEquals("key", preferenceManager.openAiApiKey)
    }

    @Test
    fun `given user reads default huggingFaceApiKey, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_HUGGING_FACE_API_KEY), any()))
            .thenReturn("00000000")

        Assert.assertEquals("00000000", preferenceManager.huggingFaceApiKey)

        whenever(stubPreference.getString(eq(KEY_HUGGING_FACE_API_KEY), any()))
            .thenReturn("key")

        preferenceManager.huggingFaceApiKey = "key"

        Assert.assertEquals("key", preferenceManager.huggingFaceApiKey)
    }

    @Test
    fun `given user reads default huggingFaceModel, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_HUGGING_FACE_MODEL_KEY), any()))
            .thenReturn(HuggingFaceModel.default.alias)

        Assert.assertEquals(HuggingFaceModel.default.alias, preferenceManager.huggingFaceModel)

        whenever(stubPreference.getString(eq(KEY_HUGGING_FACE_MODEL_KEY), any()))
            .thenReturn("key")

        preferenceManager.huggingFaceModel = "key"

        Assert.assertEquals("key", preferenceManager.huggingFaceModel)
    }

    @Test
    fun `given user reads default stabilityAiApiKey, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_STABILITY_AI_API_KEY), any()))
            .thenReturn("")

        Assert.assertEquals("", preferenceManager.stabilityAiApiKey)

        whenever(stubPreference.getString(eq(KEY_STABILITY_AI_API_KEY), any()))
            .thenReturn("key")

        preferenceManager.stabilityAiApiKey = "key"

        Assert.assertEquals("key", preferenceManager.stabilityAiApiKey)
    }

    @Test
    fun `given user reads default stabilityAiEngineId, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_STABILITY_AI_ENGINE_ID_KEY), any()))
            .thenReturn("")

        Assert.assertEquals("", preferenceManager.stabilityAiEngineId)

        whenever(stubPreference.getString(eq(KEY_STABILITY_AI_ENGINE_ID_KEY), any()))
            .thenReturn("key")

        preferenceManager.stabilityAiEngineId = "key"

        Assert.assertEquals("key", preferenceManager.stabilityAiEngineId)
    }

    @Test
    fun `given user reads default forceSetupAfterUpdate, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_FORCE_SETUP_AFTER_UPDATE), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.forceSetupAfterUpdate)

        whenever(stubPreference.getBoolean(eq(KEY_FORCE_SETUP_AFTER_UPDATE), any()))
            .thenReturn(true)

        preferenceManager.forceSetupAfterUpdate = true

        Assert.assertEquals(true, preferenceManager.forceSetupAfterUpdate)
    }

    @Test
    fun `given user reads default localModelId, changes it, expected default value, then changed value`() {
        whenever(stubPreference.getString(eq(KEY_LOCAL_MODEL_ID), any()))
            .thenReturn("")

        Assert.assertEquals("", preferenceManager.localOnnxModelId)

        whenever(stubPreference.getString(eq(KEY_LOCAL_MODEL_ID), any()))
            .thenReturn("key")

        preferenceManager.localOnnxModelId = "key"

        Assert.assertEquals("key", preferenceManager.localOnnxModelId)
    }

    @Test
    fun `given user reads default localUseNNAPI, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_LOCAL_NN_API), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.localOnnxUseNNAPI)

        whenever(stubPreference.getBoolean(eq(KEY_LOCAL_NN_API), any()))
            .thenReturn(true)

        preferenceManager.localOnnxUseNNAPI = true

        Assert.assertEquals(true, preferenceManager.localOnnxUseNNAPI)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.localUseNNAPI }
    }

    @Test
    fun `given user reads default designUseSystemColorPalette, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_DYNAMIC_COLORS), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.designUseSystemColorPalette)

        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_DYNAMIC_COLORS), any()))
            .thenReturn(true)

        preferenceManager.designUseSystemColorPalette = true

        Assert.assertEquals(true, preferenceManager.designUseSystemColorPalette)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.designUseSystemColorPalette }
    }

    @Test
    fun `given user reads default designUseSystemDarkTheme, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_SYSTEM_DARK_THEME), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.designUseSystemDarkTheme)

        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_SYSTEM_DARK_THEME), any()))
            .thenReturn(true)

        preferenceManager.designUseSystemDarkTheme = true

        Assert.assertEquals(true, preferenceManager.designUseSystemDarkTheme)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.designUseSystemDarkTheme }
    }

    @Test
    fun `given user reads default designDarkTheme, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_DARK_THEME), any()))
            .thenReturn(false)

        Assert.assertEquals(false, preferenceManager.designDarkTheme)

        whenever(stubPreference.getBoolean(eq(KEY_DESIGN_DARK_THEME), any()))
            .thenReturn(true)

        preferenceManager.designDarkTheme = true

        Assert.assertEquals(true, preferenceManager.designDarkTheme)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.designDarkTheme }
    }

    @Test
    fun `given user reads default designColorToken, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_DESIGN_COLOR_TOKEN), any()))
            .thenReturn("${ColorToken.MAUVE}")

        Assert.assertEquals("${ColorToken.MAUVE}", preferenceManager.designColorToken)

        whenever(stubPreference.getString(eq(KEY_DESIGN_COLOR_TOKEN), any()))
            .thenReturn("${ColorToken.PEACH}")

        preferenceManager.designColorToken = "${ColorToken.PEACH}"

        Assert.assertEquals("${ColorToken.PEACH}", preferenceManager.designColorToken)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.designColorToken == "${ColorToken.PEACH}" }
    }

    @Test
    fun `given user reads default designDarkThemeToken, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_DESIGN_DARK_TOKEN), any()))
            .thenReturn("${DarkThemeToken.FRAPPE}")

        Assert.assertEquals("${DarkThemeToken.FRAPPE}", preferenceManager.designDarkThemeToken)

        whenever(stubPreference.getString(eq(KEY_DESIGN_DARK_TOKEN), any()))
            .thenReturn("${DarkThemeToken.MOCHA}")

        preferenceManager.designDarkThemeToken = "${DarkThemeToken.MOCHA}"

        Assert.assertEquals("${DarkThemeToken.MOCHA}", preferenceManager.designDarkThemeToken)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.designDarkThemeToken == "${DarkThemeToken.MOCHA}" }
    }
}
