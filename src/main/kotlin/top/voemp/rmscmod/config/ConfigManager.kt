package top.voemp.rmscmod.config

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.util.LevelIdentityProvider
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConfigManager {
    private val NEW_CONFIG_NAME: Text = Text.translatable("config.rmscmod.newConfig")
    private var configName = NEW_CONFIG_NAME.string
    private var configId: String? = null
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")

    fun getConfigName(): String = configName

    fun setConfigName(name: String) {
        configName = name
    }

    fun getConfigId(): String? = configId

    fun setConfigId(id: String?) {
        configId = id
    }

    private fun generateId(): String {
        return configName.replace("[^a-zA-Z0-9_]".toRegex(), "_") + "_" + System.currentTimeMillis()
    }

    fun build(): ModConfig {
        return ModConfig(
            id = getConfigId() ?: generateId(),
            name = configName,
            time = LocalDateTime.now().format(formatter),
            switchStatus = false,
            areaSelection = SelectionManager.areaSelection,
            switchSet = if (SelectionManager.hasSwitchSelection()) SelectionManager.switchSet else null
        )
    }

    fun configDir(): Path {
        val levelName = LevelIdentityProvider.getLevelIdentifier()
        val configDir = FabricLoader.getInstance().configDir.resolve("rmscmod/${levelName}")
        Files.createDirectories(configDir)
        return configDir
    }

    fun isExistConfig(id: String): Boolean {
        return Files.exists(configDir().resolve("${id}.json"))
    }

    fun saveConfig(config: ModConfig) {
        if (isExistConfig(config.id)) deleteConfig(config.id)
        val configFile = configDir().resolve("${config.id}.json")
        Files.writeString(configFile, Gson().toJson(config))
        resetConfig()
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

    fun resolveConfig(config: ModConfig) {
        setConfigId(config.id)
        setConfigName(config.name)
        SelectionManager.clearAll()
        if (config.areaSelection != null) {
            SelectionManager.areaSelection = config.areaSelection
        }
        if (config.switchSet != null) {
            SelectionManager.switchSet = config.switchSet
        }
    }

    fun deleteConfig(id: String) {
        Files.deleteIfExists(configDir().resolve("$id.json"))
    }

    fun resetConfig() {
        SelectionManager.clearAll()
        setConfigId(null)
        setConfigName(NEW_CONFIG_NAME.string)
    }
}