package top.voemp.rmscmod.config

import net.minecraft.text.Text
import top.voemp.rmscmod.selection.SelectionManager

object ConfigCreator {
    private val NEW_CONFIG_NAME: Text = Text.translatable("config.rmscmod.newConfig")
    private var configName = NEW_CONFIG_NAME.string

    fun getConfigName(): String {
        return this.configName
    }

    fun setConfigName(name: String) {
        this.configName = name
    }

    private fun generateId(): String {
        return configName.replace("[^a-zA-Z0-9_]".toRegex(), "_") + System.currentTimeMillis()
    }

    fun build(): ModConfig {
        return ModConfig(
            id = ConfigManager.curConfigId ?: generateId(),
            name = configName,
            switchStatus = false,
            areaSelection = SelectionManager.areaSelection,
            switchSet = if (SelectionManager.hasSwitchSelection()) SelectionManager.switchSet else null
        )
    }
}