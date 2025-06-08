package top.voemp.rmscmod.serial

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import top.voemp.rmscmod.RMSCMod.MOD_ID
import top.voemp.rmscmod.RMSCMod.logger
import java.io.InputStreamReader
import java.net.URI

object FontBitmap {
    private const val FONT_URL = "https://github.com/Yuyuko9961/rmsc-arduino-uno/releases/download/Font/font_data_LSB.json"
    private const val FONT_FILE = "font.json"
    private val gson = Gson()
    private val fontDir = FabricLoader.getInstance().configDir.resolve("${MOD_ID}/${FONT_FILE}")
    private val fontData: Map<String, String> by lazy {
        val inputStream = fontDir.toFile().inputStream()
        InputStreamReader(inputStream).use { reader ->
            gson.fromJson(
                reader,
                object : TypeToken<Map<String, String>>() {}.type
            )
        }
    }

    fun initFont() {
        if (fontDir.toFile().exists() && fontDir.toFile().length() > 2) return
        fontDir.toFile().parentFile.mkdirs()
        val fontFile = fontDir.toFile()
        try {
            val connection = URI(FONT_URL).toURL().openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val outputStream = fontFile.outputStream()
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            fontFile.renameTo(fontDir.toFile())
            logger.info("Font file downloaded successfully.")
        } catch (e: Exception) {
            logger.info("Failed to download font file: ${e.message}")
            fontFile.createNewFile()
            fontFile.writeText(gson.toJson(emptyMap<String, String>()))
            logger.info("Empty font file created successfully.")
        }
    }

    fun getValue(key: String): List<UByte> = fontData[key]?.split(",")?.map { it.toUByte() } ?: emptyList()

    fun string2Unicode(str: String): List<String> = str.map { "U${it.code.toString(16)}" }

    fun of(str: String): List<UByte> = string2Unicode(str).flatMap { getValue(it) }
}