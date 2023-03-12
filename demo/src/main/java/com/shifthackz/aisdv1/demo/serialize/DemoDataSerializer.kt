package com.shifthackz.aisdv1.demo.serialize

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader

class DemoDataSerializer(private val contextProvider: () -> Context) {

    fun readDemoAssets(): List<String> {
        val rawString = contextProvider().resources.assets
            .open(FILENAME)
            .bufferedReader()
            .use(BufferedReader::readText)

        val demo = Gson().fromJson<DemoAsset>(rawString, object : TypeToken<DemoAsset>() {}.type)
        return demo.images
    }

    companion object {
        private const val FILENAME = "demo.json"
    }
}
