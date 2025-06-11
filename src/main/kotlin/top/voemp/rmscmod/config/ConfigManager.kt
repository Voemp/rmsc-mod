package top.voemp.rmscmod.config

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import top.voemp.rmscmod.RMSCMod.MOD_ID
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.serial.DataManager
import top.voemp.rmscmod.util.LevelIdentityProvider
import java.nio.file.Files
import java.nio.file.Path

object ConfigManager {
    private val NEW_CONFIG_NAME: Text = Text.translatable("config.rmscmod.newConfig")
    private val NEW_CONFIG_NAME_SUFFIX: Text = Text.translatable("config.rmscmod.newConfig.suffix", "%d")
    private var configName: String = genConfigName(NEW_CONFIG_NAME.string)
    private var editingName: String? = null

    fun getConfigName(): String = configName
    fun setConfigName(name: String) {
        configName = name
    }

    fun getEditingName(): String? = editingName
    fun setEditingName(name: String?) {
        editingName = name
    }

    fun build(): ModConfig {
        return ModConfig(
            name = if (editingName != null) configName else genConfigName(configName),
            time = System.currentTimeMillis(),
            switchStatus = false,
            areaSelection = if (SelectionManager.hasAreaSelection()) SelectionManager.areaSelection else null,
            switchSet = if (SelectionManager.hasSwitchSelection()) SelectionManager.switchSet else null
        )
    }

    fun configDir(): Path {
        val levelName = LevelIdentityProvider.getLevelIdentifier()
        val configDir = FabricLoader.getInstance().configDir.resolve("${MOD_ID}/${levelName}")
        Files.createDirectories(configDir)
        return configDir
    }

    fun isExistConfig(name: String): Boolean {
        return Files.exists(configDir().resolve("${name}.json"))
    }

    fun saveConfig(config: ModConfig) {
        if (editingName != null) deleteConfig(editingName!!)
        val configFile = configDir().resolve("${config.name}.json")
        Files.writeString(configFile, Gson().toJson(config))
        resetConfig()
        DataManager.getConfigData()
    }

    fun loadConfig(name: String): ModConfig? {
        val file = configDir().resolve("$name.json")
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
        return configs.sortedBy { it.time }.reversed()
    }

    fun resolveConfig(config: ModConfig) {
        setEditingName(config.name)
        setConfigName(config.name)
        SelectionManager.clearAll()
        if (config.areaSelection != null) {
            SelectionManager.areaSelection = config.areaSelection
        }
        if (config.switchSet != null) {
            SelectionManager.switchSet = config.switchSet
        }
    }

    fun deleteConfig(name: String) {
        Files.deleteIfExists(configDir().resolve("$name.json"))
        DataManager.getConfigData()
    }

    fun resetConfig() {
        SelectionManager.clearAll()
        setConfigName(genConfigName(NEW_CONFIG_NAME.string))
        setEditingName(null)
    }

    private fun genConfigName(name: String): String {
        if (!isExistConfig(name)) return name
        val suffixTemplate = NEW_CONFIG_NAME_SUFFIX.string
        for (i in 2..99) {
            val newName = name + String.format(suffixTemplate, i)
            if (!isExistConfig(newName)) return newName
        }
        return name + " " + System.currentTimeMillis().toString(36)
    }
}