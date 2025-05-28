package top.voemp.rmscmod.config

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import top.voemp.rmscmod.util.WorldIdentityProvider
import java.nio.file.Files
import java.nio.file.Path

object ConfigManager {
    var curConfigId: String? = null

    fun configDir(): Path {
        val worldName = WorldIdentityProvider.getWorldIdentifier()
        val configDir = FabricLoader.getInstance().configDir.resolve("rmscmod/${worldName}")
        Files.createDirectories(configDir)
        return configDir
    }

    fun isExistConfig(id: String): Boolean {
        return Files.exists(configDir().resolve("${id}.json"))
    }

    fun saveConfig(config: ModConfig) {
        val configFile = configDir().resolve("${config.id}.json")
        Files.writeString(configFile, Gson().toJson(config))
        curConfigId = null
    }

    fun loadConfig(id: String): ModConfig? {
        val file = configDir().resolve("$id.json")
        return if (Files.exists(file)) {
            Gson().fromJson(Files.readString(file), ModConfig::class.java)
        } else null
    }

    fun loadAllConfigs(): List<ModConfig> {
        val configs = mutableListOf<ModConfig>()
        Files.list(configDir()).forEach { file ->
            if (file.toString().endsWith(".json")) {
                configs.add(Gson().fromJson(Files.readString(file), ModConfig::class.java))
            }
        }
        return configs
    }

    fun deleteConfig(id: String) {
        Files.deleteIfExists(configDir().resolve("$id.json"))
    }
}