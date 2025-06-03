package top.voemp.rmscmod.gui

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.config.ModConfig
import top.voemp.rmscmod.selection.SelectionManager

@Environment(EnvType.CLIENT)
class ConfigListScreen(parent: Screen?) :
    GameOptionsScreen(parent, null, Text.translatable("menu.rmscmod.configListScreen.title")) {
    private var configSelectionList: ConfigListWidget? = null
    private var editButton: ButtonWidget? = null
    private var deleteButton: ButtonWidget? = null

    override fun init() {
        super.init()
        configSelected(null)
    }

    override fun initBody() {
        configSelectionList = layout.addBody(ConfigListWidget(client))
    }

    override fun initFooter() {
        val footerWidget = layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        footerWidget.mainPositioner.alignHorizontalCenter()
        editButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfig")
            ) { button -> onEdit() }.width(100).build()
        )
        deleteButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.deleteConfig")
            ) { button -> onDelete() }.width(100).build()
        )
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { button -> onDone() }.width(100).build()
        )
    }

    override fun initTabNavigation() {
        super.initTabNavigation()
        configSelectionList?.position(width, layout)
    }

    override fun addOptions() {}

    private fun onEdit() {
        val configEntry: ConfigListWidget.ConfigEntry? = configSelectionList?.selectedOrNull
        if (configEntry != null) {
            val config = ConfigManager.loadConfig(configEntry.id)
            config?.let { ConfigManager.resolveConfig(it) }
            client?.setScreen(EditConfigScreen(this))
        }
    }

    private fun onDelete() {
        val configEntry: ConfigListWidget.ConfigEntry? = configSelectionList?.selectedOrNull
        if (configEntry != null) ConfigManager.deleteConfig(configEntry.id)
        refreshScreen()
    }

    private fun onDone() {
        client?.setScreen(parent)
    }

    /**
     * 根据选中配置，设置按钮可用状态
     * @param config 配置
     */
    private fun configSelected(config: ModConfig?) {
        editButton?.active = config != null
        deleteButton?.active = config != null
    }

    fun refreshScreen() {
        client?.setScreen(ConfigListScreen(parent))
    }

    @Environment(EnvType.CLIENT)
    inner class ConfigListWidget(client: MinecraftClient?) :
        AlwaysSelectedEntryListWidget<ConfigListWidget.ConfigEntry>(
            client,
            width,
            height - layout.headerHeight - layout.footerHeight,
            layout.headerHeight,
            36
        ) {
        init {
            ConfigManager.loadAllConfigs().forEach {
                addEntry(ConfigEntry(it))
            }
        }

        override fun setSelected(entry: ConfigEntry?) {
            super.setSelected(entry)
            configSelected(entry?.config)
        }

        @Environment(EnvType.CLIENT)
        inner class ConfigEntry(val config: ModConfig) : Entry<ConfigEntry?>() {
            val id: String = config.id

            override fun render(
                context: DrawContext?,
                index: Int,
                y: Int,
                x: Int,
                entryWidth: Int,
                entryHeight: Int,
                mouseX: Int,
                mouseY: Int,
                hovered: Boolean,
                tickDelta: Float
            ) {
                context?.drawText(
                    client.textRenderer,
                    Text.of(config.name),
                    x + 5,
                    y + 5,
                    0xFFFFFF,
                    false
                )
                context?.drawText(
                    client.textRenderer,
                    Text.of(config.time),
                    x + entryWidth - 5 - client.textRenderer.getWidth(Text.of(config.time.toString())),
                    y + 5,
                    0x888888,
                    false
                )
            }

            override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                setSelected(this)
                return super.mouseClicked(mouseX, mouseY, button)
            }

            override fun getNarration(): Text? {
                return Text.of(config.name)
            }
        }
    }
}