package top.voemp.rmscmod.config

import net.minecraft.text.Text

object ConfigCreator {
    private val NEW_CONFIG_NAME: Text = Text.translatable("config.rmscmod.newConfig")
    private var configName = NEW_CONFIG_NAME.string

    fun getConfigName(): String {
        return this.configName
    }

    fun setConfigName(name: String) {
        this.configName = name
    }
}