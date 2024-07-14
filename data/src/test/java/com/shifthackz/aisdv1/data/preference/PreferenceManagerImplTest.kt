package com.shifthackz.aisdv1.data.preference

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_AI_AUTO_SAVE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_DEMO_MODE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_ALWAYS_SHOW_ADVANCED_OPTIONS
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_FORM_PROMPT_TAGGED_INPUT
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_MONITOR_CONNECTIVITY
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SAVE_TO_MEDIA_STORE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SD_MODEL
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_SOURCE
import com.shifthackz.aisdv1.data.preference.PreferenceManagerImpl.Companion.KEY_SERVER_URL
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PreferenceManagerImplTest {

    private val stubEditor = mock<SharedPreferences.Editor>()
    private val stubPreference = mock<SharedPreferences>()

    private val preferenceManager = PreferenceManagerImpl(stubPreference)

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
    }

    @Test
    fun `given user reads default serverUrl, changes it, expected default value, then changed value, observer emits changed value`() {
        whenever(stubPreference.getString(eq(KEY_SERVER_URL), any()))
            .thenReturn("")

        Assert.assertEquals("", preferenceManager.serverUrl)

        whenever(stubPreference.getString(eq(KEY_SERVER_URL), any()))
            .thenReturn("https://192.168.0.1:7860")

        preferenceManager.serverUrl = "https://192.168.0.1:7860"

        Assert.assertEquals("https://192.168.0.1:7860", preferenceManager.serverUrl)

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
            .thenReturn(ServerSource.LOCAL.key)

        preferenceManager.source =  ServerSource.LOCAL

        Assert.assertEquals(ServerSource.LOCAL, preferenceManager.source)

        preferenceManager
            .observe()
            .test()
            .assertNoErrors()
            .assertValueAt(0) { settings -> settings.source == ServerSource.LOCAL }
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
}
