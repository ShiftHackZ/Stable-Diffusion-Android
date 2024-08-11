@file:Suppress("KotlinConstantConditions")

package com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import android.text.TextUtils
import android.util.JsonReader
import android.util.Pair
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.KEY_INPUT_IDS
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT_KEY_MODEL_FORMAT
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.TAG
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.halfCorner
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.toArrays
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.extensions.modelPathPrefix
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.IntBuffer
import java.util.Arrays
import java.util.Locale
import java.util.regex.Pattern

internal class EnglishTextTokenizer(
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val localModelIdProvider: LocalModelIdProvider,
) : LocalDiffusionTextTokenizer {

    private val pattern = Pattern.compile(TOKENIZER_REGEX)

    private val encoder: MutableMap<String, Int> = HashMap()
    private val decoder: MutableMap<Int, String> = HashMap()
    private val bpeRanks: MutableMap<Pair<String, String>, Int?> = HashMap()

    private var isInitMap = false
    private var session: OrtSession? = null

    override val maxLength = 77

    override fun initialize() {
        if (session != null) {
            debugLog("{$TAG} {TOKENIZER} {initialize} Session already initialized, skipping...")
            return
        }
        val options = OrtSession.SessionOptions()
        options.addConfigEntry(ORT_KEY_MODEL_FORMAT, ORT)
        session = ortEnvironmentProvider.get().createSession(
            "${modelPathPrefix(fileProviderDescriptor, localModelIdProvider)}/${LocalDiffusionContract.TOKENIZER_MODEL}",
            options
        )
        debugLog("{$TAG} {TOKENIZER} {initialize} Session created successfully!")
        if (!isInitMap) {
            encoder.putAll(loadEncoder())
            decoder.putAll(loadDecoder(encoder))
            bpeRanks.putAll(loadBpeRanks())
        }
        isInitMap = true
        debugLog("{$TAG} {TOKENIZER} {initialize} Tokenizer map initialized successfully!")
    }

    override fun decode(ids: IntArray?): String {
        debugLog("{$TAG} {TOKENIZER} {decode} Trying to decode ${ids?.size ?: "null"} int array...")
        if (ids == null) {
            debugLog("{$TAG} {TOKENIZER} {decode} Input ids array is null, skipping.")
            return ""
        }
        val stringBuilder = StringBuilder()
        for (value in ids) {
            if (decoder.containsKey(value)) stringBuilder.append(decoder[value])
        }
        val result: MutableList<Int> = ArrayList()
        for (element in stringBuilder) {
            val key = element.toString()
            if (TokenizerByteSet.byteDecoder.containsKey(key)) {
                TokenizerByteSet.byteDecoder[key]?.let { result.add(it) }
            }
        }
        val ints = IntArray(result.size)
        for (i in result.indices) ints[i] = result[i]
        val resultString =  String(ints, 0, ints.size)
        debugLog("{$TAG} {TOKENIZER} {decode} Decode was successful!")
        return resultString
    }

    override fun encode(text: String?): IntArray {
        debugLog("{$TAG} {TOKENIZER} {encode} Trying to encode ${text ?: "null"}...")
        var input = text
        input = input.toString().lowercase(Locale.getDefault()).halfCorner()
        val stringList: MutableList<String> = ArrayList()
        val matcher = pattern.matcher(input)
        while (matcher.find()) {
            val result = matcher.toMatchResult()
            val value = result.group().trim { it <= ' ' }
            val sb = StringBuilder()
            val bytes = value.toByteArray()
            val array = IntArray(bytes.size)
            for (i in array.indices) array[i] = bytes[i].toInt() and 0xff
            for (o in array) {
                if (TokenizerByteSet.byteEncoder.containsKey(o)) {
                    sb.append(TokenizerByteSet.byteEncoder[o])
                }
            }
            stringList.add(sb.toString())
        }
        val strings: MutableList<String> = ArrayList()
        for (string in stringList) {
            strings.addAll(bpe(string))
        }
        val result: MutableList<Int> = ArrayList()
        result.add(49406)
        for (word in strings) {
            if (encoder.containsKey(word)) {
                result.add(encoder[word]!!)
            }
        }
        val ids = IntArray(result.size)
        for (i in ids.indices) ids[i] = result[i]
        val copy = IntArray(maxLength)
        Arrays.fill(copy, 49407)
        System.arraycopy(ids, 0, copy, 0, if (ids.size < copy.size) ids.size else copy.size)
        copy[copy.size - 1] = 49407
        debugLog("{$TAG} {TOKENIZER} {encode} Encode was successful!")
        return copy
    }

    override fun tensor(ids: IntArray?): OnnxTensor? {
        debugLog("{$TAG} {TOKENIZER} {tensor} Trying to tensor ${ids?.size ?: "null"} int array...")
        if (ids == null) {
            debugLog("{$TAG} {TOKENIZER} {tensor} Input ids array is null, skipping.")
            return null
        }
        val inputIds = OnnxTensor.createTensor(
            ortEnvironmentProvider.get(),
            IntBuffer.wrap(ids),
            longArrayOf(1, ids.size.toLong())
        )
        val input: MutableMap<String, OnnxTensor> = HashMap()
        input[KEY_INPUT_IDS] = inputIds
        val result = session!!.run(input)
        val lastHiddenState = result[0].value
        result.close()
        val tensor = OnnxTensor.createTensor(ortEnvironmentProvider.get(), lastHiddenState)
        debugLog("{$TAG} {TOKENIZER} {tensor} Tensor formation was successful!")
        return tensor
    }

    override fun createUnconditionalInput(text: String?): IntArray = encode(text)

    override fun close() {
        debugLog("{$TAG} {TOKENIZER} {close} Closing session...")
        session?.close()
        session = null
        debugLog("{$TAG} {TOKENIZER} {close} Session closed successfully!")
    }

    private fun bpe(token: String): List<String> {
        if (TextUtils.isEmpty(token)) {
            return listOf(token)
        }
        var word: MutableList<String?> = token.toArrays().toMutableList()
        val lastItem = word.removeAt(word.size - 1)
        word.add("$lastItem</w>")
        var pairs = getPairs(word.toList())
        while (true) {
            var min: Pair<String, String>? = null
            var minValue = 0
            for (pair in pairs) {
                if (!bpeRanks.containsKey(pair)) {
                    continue
                }
                val value = bpeRanks[pair]!!
                if (min == null || value < minValue) {
                    min = pair
                    minValue = value
                }
            }
            if (min == null) break
            var i = 0
            val newWord: MutableList<String?> = ArrayList()
            while (i < word.size) {
                var j = -1
                for (x in word.indices) {
                    if (x >= i && word[x] == min.first) {
                        j = x
                        break
                    }
                }
                i = if (j != -1) {
                    newWord.addAll(word.subList(i, j).filterNotNull())
                    j
                } else {
                    newWord.addAll(word.subList(i, word.size).filterNotNull())
                    break
                }
                i += if (word[i] == min.first && i < word.size - 1 && word[i + 1] == min.second) {
                    newWord.add(min.first + min.second)
                    2
                } else {
                    word[i]?.let { newWord.add(it) }
                    1
                }
            }
            word = newWord
            pairs = if (word.size == 1) {
                break
            } else {
                getPairs(word)
            }
        }
        return word.filterNotNull()
    }

    private fun getPairs(word: List<String?>): Set<Pair<String, String>> {
        val result: MutableSet<Pair<String, String>> = LinkedHashSet()
        for (i in 0 until word.size - 1) {
            result.add(Pair(word[i], word[i + 1]))
        }
        return result
    }

    private fun loadEncoder(): Map<String, Int> {
        val map: MutableMap<String, Int> = HashMap()
        try {
            val path = "${modelPathPrefix(fileProviderDescriptor, localModelIdProvider)}/${LocalDiffusionContract.TOKENIZER_VOCABULARY}"
            val jsonReader = JsonReader(InputStreamReader(FileInputStream(path)))
            jsonReader.beginObject()
            while (jsonReader.hasNext()) {
                val key = jsonReader.nextName()
                val value = jsonReader.nextInt()
                map[key] = value
            }
            jsonReader.close()
        } catch (e: Exception) {
            errorLog(e)
        }
        return map
    }

    private fun loadDecoder(encoder: Map<String, Int>): Map<Int, String> {
        val result: MutableMap<Int, String> = HashMap(encoder.size)
        for (entry in encoder) {
            result[entry.value] = entry.key
        }
        return result
    }

    private fun loadBpeRanks(): Map<Pair<String, String>, Int?> {
        val result: MutableMap<Pair<String, String>, Int?> = HashMap()
        try {
            val path = "${modelPathPrefix(fileProviderDescriptor, localModelIdProvider)}/${LocalDiffusionContract.TOKENIZER_MERGES}"
            val reader = BufferedReader(InputStreamReader(FileInputStream(path)))
            var line: String
            var startLine = 1
            var count = 0
            while (reader.readLine().also { ln -> line = ln ?: "" } != null) {
                if (startLine != 0 && startLine-- > 0) continue
                val array = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (array.size >= 2) {
                    result[Pair(array[0], array[1])] =
                        count++
                }
            }
            reader.close()
        } catch (e: Exception) {
            errorLog(e)
        }
        return result
    }

    companion object {
        private const val TOKENIZER_REGEX = "'s|'t|'re|'ve|'m|'ll|'d| ?\\p{L}+| ?\\p{N}+| ?[^\\s\\p{L}\\p{N}]+|\\s+(?!\\S)|\\s+"
    }
}
