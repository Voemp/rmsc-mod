package top.voemp.rmscmod.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.gui.tab.GridScreenTab
import net.minecraft.client.gui.tab.TabManager
import net.minecraft.client.gui.widget.*
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import top.voemp.rmscmod.config.ConfigCreator
import top.voemp.rmscmod.option.ModKeyBinding
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

    override fun init() {/*
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        // 文本输入框
        nameField = TextFieldWidget(
            textRenderer,
            this.width / 2 - 100,
            this.height / 2 - 30,
            200, 20,
            Text.literal("输入名称")
        )
        nameField.setMaxLength(32)
        nameField.text = ""

        // 保存按钮
        saveButton = ButtonWidget
            .builder(Text.literal("保存")) {
                val name = nameField.text
                // 保存逻辑

                client.setScreen(null)
            }
            .position(this.width / 2 - 100, this.height / 2)
            .size(95, 20)
            .build()

        // 取消按钮
        cancelButton = ButtonWidget
            .builder(Text.literal("取消")) {
                client.setScreen(null)
            }
            .position(this.width / 2 + 5, this.height / 2)
            .size(95, 20)
            .build()

        addDrawableChild(nameField)
        addDrawableChild(saveButton)
        addDrawableChild(cancelButton)
         */
        this.tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(SaveTab(), SaveTab()).build()
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
        footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.tab.save.saveConfig")
            ) { button -> {} }.build()
        )
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


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return super.mouseClicked(mouseX, mouseY, button)
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

        private var p1: BlockPos? = SelectionManager.point1
        private var p2: BlockPos? = SelectionManager.point2
        private var switchSet: Set<BlockPos> = SelectionManager.switchPosSet

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
                SelectionManager.point1 = BlockPos(x, p1?.y ?: 0, p1?.z ?: 0)
            }
            p1YField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P1 Y"))
            p1YField.text = p1?.y?.toString() ?: ""
            p1YField.setChangedListener { yText ->
                val y = yText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.point1 = BlockPos(p1?.x ?: 0, y, p1?.z ?: 0)
            }
            p1ZField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P1 Z"))
            p1ZField.text = p1?.z?.toString() ?: ""
            p1ZField.setChangedListener { zText ->
                val z = zText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.point1 = BlockPos(p1?.x ?: 0, p1?.y ?: 0, z)
            }
            p2XField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 X"))
            p2XField.text = p2?.x?.toString() ?: ""
            p2XField.setChangedListener { xText ->
                val x = xText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.point2 = BlockPos(x, p2?.y ?: 0, p2?.z ?: 0)
            }
            p2YField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 Y"))
            p2YField.text = p2?.y?.toString() ?: ""
            p2YField.setChangedListener { yText ->
                val y = yText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.point2 = BlockPos(p2?.x ?: 0, y, p2?.z ?: 0)
            }
            p2ZField = TextFieldWidget(textRenderer, 45, 20, Text.literal("P2 Z"))
            p2ZField.text = p2?.z?.toString() ?: ""
            p2ZField.setChangedListener { zText ->
                val z = zText.toIntOrNull() ?: return@setChangedListener
                SelectionManager.point2 = BlockPos(p2?.x ?: 0, p2?.y ?: 0, z)
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
                        SelectionManager.areaSelectionWorld?.value?.toString() ?: "None"
                    ), textRenderer
                )
            )

            clearButtons = DirectionalLayoutWidget.horizontal().spacing(8)
            clearButtons.add(
                ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.clearAreaSelection")
                ) {
                    SelectionManager.clearAreaSelection()
                    clearAreaPointFields()
                }.width(66).build()
            )
            clearButtons.add(
                ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.clearSwitchSelection")
                ) {
                    SelectionManager.clearSwitchSelection()
                    removeSwitchPosFields()
                }.width(66).build()
            )
            leftColumn.add(clearButtons)

            switchPosListContainer = DirectionalLayoutWidget.vertical().spacing(8)
            switchSet.forEachIndexed { index, pos ->
                val xField = TextFieldWidget(textRenderer, 45, 20, Text.literal("X"))
                xField.text = pos.x.toString()
                xField.setChangedListener { xText ->
                    val x = xText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.switchPosSet = switchSet.toMutableSet().apply {
                        remove(pos)
                        add(BlockPos(x, pos.y, pos.z))
                    }
                }
                val yField = TextFieldWidget(textRenderer, 45, 20, Text.literal("Y"))
                yField.text = pos.y.toString()
                yField.setChangedListener { yText ->
                    val y = yText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.switchPosSet = switchSet.toMutableSet().apply {
                        remove(pos)
                        add(BlockPos(pos.x, y, pos.z))
                    }
                }
                val zField = TextFieldWidget(textRenderer, 45, 20, Text.literal("Z"))
                zField.text = pos.z.toString()
                zField.setChangedListener { zText ->
                    val z = zText.toIntOrNull() ?: return@setChangedListener
                    SelectionManager.switchPosSet = switchSet.toMutableSet().apply {
                        remove(pos)
                        add(BlockPos(pos.x, pos.y, z))
                    }
                }
                val removeButton = ButtonWidget.builder(
                    Text.translatable("menu.rmscmod.tab.save.removeSwitch")
                ) {
                    SelectionManager.switchPosSet = switchSet.toMutableSet().apply { remove(pos) }
                    switchSet = SelectionManager.switchPosSet
                    switchPosListContainer.forEachElement { container ->
                        container.forEachChild { widget ->
                            if (widget.isSelected) {
                                container.forEachChild {
                                    remove(it)
                                }
                            }
                        }
                    }
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
                        Text.literal("Switch ${index + 1}:")
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

            /*
            val cyclingButtonWidget = adder.add<CyclingButtonWidget<WorldCreator.Mode?>?>(
                CyclingButtonWidget.builder<WorldCreator.Mode?>(Function { value: WorldCreator.Mode? -> value!!.name })
                    .values(WorldCreator.Mode.SURVIVAL, WorldCreator.Mode.HARDCORE, WorldCreator.Mode.CREATIVE)
                    .build(
                        0,
                        0,
                        210,
                        20,
                        CreateWorldScreen.GAME_MODE_TEXT,
                        UpdateCallback { button: CyclingButtonWidget<WorldCreator.Mode?>?, value: WorldCreator.Mode? ->
                            this@CreateWorldScreen.worldCreator.setGameMode(value)
                        }),
                positioner
            )
            this@CreateWorldScreen.worldCreator.addListener(Consumer { creator: WorldCreator? ->
                cyclingButtonWidget.setValue(creator!!.getGameMode())
                cyclingButtonWidget.active = !creator.isDebug()
                cyclingButtonWidget.setTooltip(Tooltip.of(creator.getGameMode().getInfo()))
            })
            val cyclingButtonWidget2 = adder.add<CyclingButtonWidget<Difficulty?>?>(
                CyclingButtonWidget.builder<Difficulty?>(Function { obj: Difficulty? -> obj!!.getTranslatableName() })
                    .values(*Difficulty.entries.toTypedArray())
                    .build(
                        0,
                        0,
                        210,
                        20,
                        Text.translatable("options.difficulty"),
                        UpdateCallback { button: CyclingButtonWidget<Difficulty?>?, value: Difficulty? ->
                            this@CreateWorldScreen.worldCreator.setDifficulty(value)
                        }),
                positioner
            )
            this@CreateWorldScreen.worldCreator.addListener(Consumer { creator: WorldCreator? ->
                cyclingButtonWidget2.setValue(this@CreateWorldScreen.worldCreator.getDifficulty())
                cyclingButtonWidget2.active = !this@CreateWorldScreen.worldCreator.isHardcore()
                cyclingButtonWidget2.setTooltip(
                    Tooltip.of(
                        this@CreateWorldScreen.worldCreator.getDifficulty().getInfo()
                    )
                )
            })
            val cyclingButtonWidget3 = adder.add<CyclingButtonWidget<Boolean?>?>(
                CyclingButtonWidget.onOffBuilder()
                    .tooltip(TooltipFactory { value: Boolean? -> Tooltip.of(CreateWorldScreen.ALLOW_COMMANDS_INFO_TEXT) })
                    .build(
                        0,
                        0,
                        210,
                        20,
                        ALLOW_COMMANDS_TEXT,
                        UpdateCallback { button: CyclingButtonWidget<Boolean?>?, value: Boolean? ->
                            this@CreateWorldScreen.worldCreator.setCheatsEnabled(value)
                        })
            )
            this@CreateWorldScreen.worldCreator.addListener(Consumer { creator: WorldCreator? ->
                cyclingButtonWidget3.setValue(this@CreateWorldScreen.worldCreator.areCheatsEnabled())
                cyclingButtonWidget3.active =
                    !this@CreateWorldScreen.worldCreator.isDebug() && !this@CreateWorldScreen.worldCreator.isHardcore()
            })
            if (!SharedConstants.getGameVersion().isStable()) {
                adder.add<ButtonWidget?>(
                    ButtonWidget.builder(
                        CreateWorldScreen.EXPERIMENTS_TEXT,
                        PressAction { button: ButtonWidget? ->
                            this@CreateWorldScreen.openExperimentsScreen(
                                this@CreateWorldScreen.worldCreator.getGeneratorOptionsHolder().dataConfiguration()
                            )
                        }
                    )
                        .width(210)
                        .build()
                )
            }
            */
        }

        private fun clearAreaPointFields() {
            p1XField.text = ""
            p1YField.text = ""
            p1ZField.text = ""
            p2XField.text = ""
            p2YField.text = ""
            p2ZField.text = ""
        }

        private fun removeSwitchPosFields() {
            switchPosListContainer.forEachChild {
                remove(it)
            }
        }
    }
}