package top.voemp.rmscmod.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.tab.GridScreenTab
import net.minecraft.client.gui.tab.TabManager
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.*
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import top.voemp.rmscmod.config.ConfigCreator
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.selection.BlockPosWithWorld
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.util.GuiUtils.createHorizontalLabeledWidget

@Environment(EnvType.CLIENT)
class ModMenuScreen : Screen(Text.translatable("menu.rmscmod.title")) {
    private val layout = ThreePartsLayoutWidget(this)
    private val tabManager =
        TabManager({ drawableElement -> this.addDrawableChild(drawableElement) }, { child -> this.remove(child) })
    private var tabNavigation: TabNavigationWidget? = null
    val configCreator: ConfigCreator = ConfigCreator

    companion object {
        val ENTER_NAME_TEXT: Text = Text.translatable("menu.rmscmod.tab.save.enterName")
        private val SAVE_TAB_TITLE_TEXT: Text = Text.translatable("menu.rmscmod.tab.save.title")
    }

    override fun init() {
        this.tabNavigation = TabNavigationWidget
            .builder(this.tabManager, this.width)
            .tabs(SaveTab(), SaveTab())
            .build()
        this.addDrawableChild(this.tabNavigation)
        this.initFooter()

        this.layout.forEachChild { child ->
            child.navigationOrder = 1
            this.addDrawableChild(child)
        }
        this.tabNavigation?.selectTab(0, false)
        this.initTabNavigation()
    }

    private fun initFooter() {
        val footerWidget: DirectionalLayoutWidget =
            this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        val saveButton = ButtonWidget.builder(
            Text.translatable("menu.rmscmod.tab.save.saveConfig")
        ) {
            val config = ConfigCreator.build()
            val confirmScreen: ConfirmScreen?
            if (ConfigManager.isExistConfig(config.id)) {
                confirmScreen = ConfirmScreen(
                    { confirmed ->
                        if (confirmed) {
                            ConfigManager.deleteConfig(config.id)
                            ConfigManager.saveConfig(config)
                            SelectionManager.clearAll()
                        }
                        refreshScreen()
                    },
                    Text.translatable("menu.rmscmod.tab.save.confirmCover"),
                    Text.translatable("menu.rmscmod.tab.save.confirmCover.message")
                )
            } else {
                confirmScreen = ConfirmScreen(
                    { confirmed ->
                        if (confirmed) {
                            ConfigManager.saveConfig(config)
                            SelectionManager.clearAll()
                        }
                        refreshScreen()
                    },
                    Text.translatable("menu.rmscmod.tab.save.confirmSave"),
                    Text.translatable("menu.rmscmod.tab.save.confirmSave.message")
                )
            }
            this.client?.setScreen(confirmScreen)
        }.build()
        saveButton.active = SelectionManager.hasAreaSelection() || SelectionManager.hasSwitchSelection()
        if (!saveButton.active) {
            saveButton.tooltip = Tooltip.of(Text.literal("请先选择区域或开关"))
        }
        footerWidget.add(saveButton)
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { MinecraftClient.getInstance().setScreen(null) }.build()
        )
    }

    public override fun initTabNavigation() {
        if (this.tabNavigation != null) {
            this.tabNavigation!!.setWidth(this.width)
            this.tabNavigation!!.init()
            val i: Int = this.tabNavigation!!.navigationFocus.bottom
            val screenRect = ScreenRect(0, i, this.width, this.height - this.layout.footerHeight - i)
            this.tabManager.setTabArea(screenRect)
            this.layout.headerHeight = i
            this.layout.refreshPositions()
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (this.tabNavigation!!.trySwitchTabsWithKey(keyCode)) {
            return true
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true
        } else if (ModKeyBinding.OPEN_MENU.matchesKey(keyCode, scanCode)) {
            MinecraftClient.getInstance().setScreen(null)
            return true
        } else {
            return true
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        RenderSystem.enableBlend()
        context.drawTexture(
            FOOTER_SEPARATOR_TEXTURE,
            0,
            this.height - this.layout.footerHeight - 2,
            0.0f,
            0.0f,
            this.width,
            2,
            32,
            2
        )
        RenderSystem.disableBlend()
    }

    override fun renderDarkening(context: DrawContext) {
        context.drawTexture(
            CreateWorldScreen.TAB_HEADER_BACKGROUND_TEXTURE,
            0,
            0,
            0.0f,
            0.0f,
            this.width,
            this.layout.headerHeight,
            16,
            16
        )
        this.renderDarkening(context, 0, this.layout.headerHeight, this.width, this.height)
    }

    private fun refreshScreen() {
        this.client?.setScreen(ModMenuScreen())
    }

    @Environment(EnvType.CLIENT)
    inner class SaveTab : GridScreenTab(SAVE_TAB_TITLE_TEXT) {
        private val configNameField: TextFieldWidget
        private val p1XField: TextFieldWidget
        private val p1YField: TextFieldWidget
        private val p1ZField: TextFieldWidget
        private val p2XField: TextFieldWidget
        private val p2YField: TextFieldWidget
        private val p2ZField: TextFieldWidget
        private val p1FieldContainer: DirectionalLayoutWidget
        private val p2FieldContainer: DirectionalLayoutWidget
        private val pointFieldContainer: DirectionalLayoutWidget
        private val switchPosListContainer: DirectionalLayoutWidget
        private val clearButtons: DirectionalLayoutWidget

        private var p1: BlockPos? = SelectionManager.areaSelection.pos1
        private var p2: BlockPos? = SelectionManager.areaSelection.pos2
        private var switchSet: MutableSet<BlockPosWithWorld> = SelectionManager.switchSet

        init {
            val adder = this.grid.setColumnSpacing(32).createAdder(2)
            val leftColumn = DirectionalLayoutWidget.vertical().spacing(8)
            val rightColumn = DirectionalLayoutWidget.vertical().spacing(16)
            adder.add(leftColumn, 1)
            adder.add(rightColumn, 1)

            configNameField = TextFieldWidget(
                textRenderer, 140, 20, ENTER_NAME_TEXT
            )
            configNameField.text = configCreator.getConfigName()
            configNameField.setChangedListener { configName ->
                configCreator.setConfigName(configName)
            }
            setInitialFocus(configNameField)
            leftColumn.add(
                LayoutWidgets.createLabeledWidget(
                    textRenderer, configNameField, ENTER_NAME_TEXT
                )
            )

            p1XField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P1 X"))
            p1XField.text = p1?.x?.toString() ?: ""
            p1XField.setChangedListener { xText ->
                val x = xText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos1X(x)
            }
            p1YField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P1 Y"))
            p1YField.text = p1?.y?.toString() ?: ""
            p1YField.setChangedListener { yText ->
                val y = yText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos1Y(y)
            }
            p1ZField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P1 Z"))
            p1ZField.text = p1?.z?.toString() ?: ""
            p1ZField.setChangedListener { zText ->
                val z = zText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos1Z(z)
            }
            p2XField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 X"))
            p2XField.text = p2?.x?.toString() ?: ""
            p2XField.setChangedListener { xText ->
                val x = xText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos2X(x)
            }
            p2YField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 Y"))
            p2YField.text = p2?.y?.toString() ?: ""
            p2YField.setChangedListener { yText ->
                val y = yText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos2Y(y)
            }
            p2ZField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 Z"))
            p2ZField.text = p2?.z?.toString() ?: ""
            p2ZField.setChangedListener { zText ->
                val z = zText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.areaSelection.setPos2Z(z)
            }
            p1FieldContainer = DirectionalLayoutWidget.vertical().spacing(2)
            p1FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p1XField,
                    Text.literal("X:")
                )
            )
            p1FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p1YField,
                    Text.literal("Y:")
                )
            )
            p1FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p1ZField,
                    Text.literal("Z:")
                )
            )
            p2FieldContainer = DirectionalLayoutWidget.vertical().spacing(2)
            p2FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p2XField,
                    Text.literal("X:")
                )
            )
            p2FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p2YField,
                    Text.literal("Y:")
                )
            )
            p2FieldContainer.add(
                createHorizontalLabeledWidget(
                    textRenderer,
                    p2ZField,
                    Text.literal("Z:")
                )
            )
            pointFieldContainer = DirectionalLayoutWidget.horizontal().spacing(16)
            pointFieldContainer.add(
                LayoutWidgets.createLabeledWidget(
                    textRenderer,
                    p1FieldContainer,
                    Text.literal("Point 1")
                )
            )
            pointFieldContainer.add(
                LayoutWidgets.createLabeledWidget(
                    textRenderer,
                    p2FieldContainer,
                    Text.literal("Point 2")
                )
            )
            leftColumn.add(pointFieldContainer)
            leftColumn.add(
                TextWidget(
                    Text.translatable(
                        "menu.rmscmod.tab.save.world",
                        SelectionManager.areaSelection.world ?: "None"
                    ), textRenderer
                )
            )

            clearButtons = DirectionalLayoutWidget.horizontal().spacing(8)
            clearButtons.add(
                ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.clearAreaSelection")
                ) {
                    SelectionManager.clearAreaSelection()
                    refreshScreen()
                }.width(66).build()
            )
            clearButtons.add(
                ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.clearSwitchSelection")
                ) {
                    SelectionManager.clearSwitchSelection()
                    refreshScreen()
                }.width(66).build()
            )
            leftColumn.add(clearButtons)

            switchPosListContainer = DirectionalLayoutWidget.vertical().spacing(8)
            switchSet.forEachIndexed { index, switch ->
                val xField = TextFieldWidget(textRenderer, 45, 20, Text.literal("X"))
                xField.text = switch.pos.x.toString()
                xField.setChangedListener { xText ->
                    val x = xText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.removeSwitch(switch)
                    SelectionManager.addSwitch(switch.setX(x))
                }
                val yField = TextFieldWidget(textRenderer, 45, 20, Text.literal("Y"))
                yField.text = switch.pos.y.toString()
                yField.setChangedListener { yText ->
                    val y = yText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.removeSwitch(switch)
                    SelectionManager.addSwitch(switch.setY(y))
                }
                val zField = TextFieldWidget(textRenderer, 45, 20, Text.literal("Z"))
                zField.text = switch.pos.z.toString()
                zField.setChangedListener { zText ->
                    val z = zText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.removeSwitch(switch)
                    SelectionManager.addSwitch(switch.setZ(z))
                }
                val removeButton = ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.removeSwitch")
                ) {
                    SelectionManager.removeSwitch(switch)
                    refreshScreen()
                }.width(40).build()

                val switchPosFieldContainer = DirectionalLayoutWidget.horizontal().spacing(2)
                switchPosFieldContainer.add(xField)
                switchPosFieldContainer.add(yField)
                switchPosFieldContainer.add(zField)
                switchPosFieldContainer.add(removeButton)

                switchPosListContainer.add(
                    LayoutWidgets.createLabeledWidget(
                        textRenderer,
                        switchPosFieldContainer,
                        Text.literal("Switch ${index + 1}: [${switch.world}]")
                    )
                )
            }
            if (switchSet.isEmpty()) {
                rightColumn.add(
                    TextWidget(140, 160, Text.translatable("menu.rmscmod.tab.save.noSwitch"), textRenderer)
                )
            } else {
                rightColumn.add(switchPosListContainer)
            }
        }
    }
}