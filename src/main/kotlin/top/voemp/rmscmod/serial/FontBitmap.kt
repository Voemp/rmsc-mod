package top.voemp.rmscmod.serial

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fabricmc.loader.api.FabricLoader
import top.voemp.rmscmod.RMSCMod.MOD_ID
import top.voemp.rmscmod.RMSCMod.logger
import java.io.File
import java.io.InputStreamReader
import java.net.URI

object FontBitmap {
    private const val FONT_URL =
        "https://github.com/Yuyuko9961/rmsc-arduino-uno/releases/download/Font/font_data_LSB.json"
    private const val FONT_FILE = "font.json"
    private val gson = Gson()
    private val fontDir = FabricLoader.getInstance().configDir.resolve("${MOD_ID}/${FONT_FILE}")
    private var fontData: Map<String, String>? = null

    fun init() {
        CoroutineScope(Dispatchers.IO).launch { loadFont() }
    }

    private suspend fun loadFont(): Map<String, String> {
        val file = fontDir.toFile()
        if (!file.exists() || file.length() <= 2) downloadFont(file)
        return withContext(Dispatchers.IO) {
            InputStreamReader(file.inputStream()).use { reader ->
                gson.fromJson<Map<String, String>>(
                    reader,
                    object : TypeToken<Map<String, String>>() {}.type
                )
            }
        }.also {
            fontData = it
        }
    }

    private suspend fun downloadFont(file: File) = withContext(Dispatchers.IO) {
        fontDir.toFile().parentFile.mkdirs()
        try {
            val connection = URI(FONT_URL).toURL().openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val outputStream = file.outputStream()
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.renameTo(fontDir.toFile())
            logger.info("Font file downloaded successfully.")
        } catch (e: Exception) {
            logger.info("Failed to download font file: ${e.message}")
            file.createNewFile()
            file.writeText(gson.toJson(emptyMap<String, String>()))
            logger.info("Empty font file created successfully.")
        }
    }

    fun getValue(key: String): List<Byte> = fontData?.get(key)?.split(",")?.map { it.toUByte().toByte() } ?: emptyList()

    fun string2Unicode(str: String): List<String> = str.map { "U%04x".format(it.code) }

    fun of(str: String): List<Byte> = string2Unicode(str).flatMap { getValue(it) }
}