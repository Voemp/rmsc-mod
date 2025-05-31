package top.voemp.rmscmod.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.*
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.util.GuiUtils.createLabeledPosField
import top.voemp.rmscmod.util.GuiUtils.createPosField

@Environment(EnvType.CLIENT)
class EditConfigScreen(parent: Screen?) :
    GameOptionsScreen(parent, null, Text.translatable("menu.rmscmod.editConfigScreen.title")) {
    companion object {
        private val ENTER_NAME_TEXT: Text = Text.translatable("menu.rmscmod.editConfigScreen.enterName")
    }

    override fun initBody() {
        val grid = layout.addBody(GridWidget()).setColumnSpacing(32).createAdder(2)
        val leftColumn = grid.add(DirectionalLayoutWidget.vertical().spacing(8))
        val rightColumn = grid.add(DirectionalLayoutWidget.vertical().spacing(16))
        val configNameField = LabeledFieldWidget(
            textRenderer,
            140,
            20,
            ENTER_NAME_TEXT,
            ConfigManager.getConfigName(),
        ) { newName ->
            ConfigManager.setConfigName(newName)
        }
        val areaSelectionPos = DirectionalLayoutWidget.horizontal().spacing(16)
        areaSelectionPos.add(
            VerticalLabeledBlockPosWidget(
                textRenderer,
                Text.translatable("menu.rmscmod.editConfigScreen.areaPos", "1"),
                SelectionManager.areaSelection.pos1
            ) { x, y, z ->
                SelectionManager.areaSelection.setPos1(x, y, z)
            }
        )
        areaSelectionPos.add(
            VerticalLabeledBlockPosWidget(
                textRenderer,
                Text.translatable("menu.rmscmod.editConfigScreen.areaPos", "2"),
                SelectionManager.areaSelection.pos2
            ) { x, y, z ->
                SelectionManager.areaSelection.setPos2(x, y, z)
            }
        )
        val areaSelectionWorld = TextWidget(
            Text.translatable(
                "menu.rmscmod.editConfigScreen.world",
                SelectionManager.areaSelection.world ?: "None"
            ), textRenderer
        )
        val clearButtons = DirectionalLayoutWidget.horizontal().spacing(8)
        clearButtons.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfigScreen.clearAreaSelection")
            ) {
                SelectionManager.clearAreaSelection()
                refreshScreen()
            }.width(66).build()
        )
        clearButtons.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfigScreen.clearSwitchSelection")
            ) {
                SelectionManager.clearSwitchSelection()
                refreshScreen()
            }.width(66).build()
        )

        val switchSelectionList = DirectionalLayoutWidget.vertical().spacing(8)
        SelectionManager.switchSet.forEachIndexed { index, switch ->
            switchSelectionList.add(
                SwitchPosWidget(
                    textRenderer,
                    Text.translatable("menu.rmscmod.editConfigScreen.switchPos", index + 1, switch.world),
                    switch.pos,
                    { x, y, z -> switch.setPos(x, y, z) },
                    {
                        SelectionManager.removeSwitch(switch)
                        refreshScreen()
                    }
                )
            )
        }

        leftColumn.add(configNameField)
        leftColumn.add(areaSelectionPos)
        leftColumn.add(areaSelectionWorld)
        leftColumn.add(clearButtons)
        if (SelectionManager.hasSwitchSelection()) {
            rightColumn.add(switchSelectionList)
        } else {
            rightColumn.add(
                TextWidget(140, 160, Text.translatable("menu.rmscmod.tab.save.noSwitch"), textRenderer)
            )
        }
    }

    override fun initFooter() {
        val footerWidget: DirectionalLayoutWidget =
            layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        val saveButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfigScreen.saveConfig")
            ) {
                val config = ConfigManager.build()
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
                        Text.translatable("menu.rmscmod.editConfigScreen.confirmCover"),
                        Text.translatable("menu.rmscmod.editConfigScreen.confirmCover.message")
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
                        Text.translatable("menu.rmscmod.editConfigScreen.confirmSave"),
                        Text.translatable("menu.rmscmod.editConfigScreen.confirmSave.message")
                    )
                }
                client?.setScreen(confirmScreen)
            }.build()
        )
        saveButton.active = SelectionManager.hasAreaSelection() || SelectionManager.hasSwitchSelection()
        if (!saveButton.active) {
            saveButton.tooltip = Tooltip.of(Text.literal("请先选择区域或开关"))
        }
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { onDone() }.build()
        )
    }

    override fun addOptions() {}

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        RenderSystem.enableBlend()
        context.drawTexture(
            HEADER_SEPARATOR_TEXTURE,
            0,
            layout.headerHeight - 2,
            0.0f,
            0.0f,
            width,
            2,
            32,
            2
        )
        context.drawTexture(
            FOOTER_SEPARATOR_TEXTURE,
            0,
            height - layout.footerHeight,
            0.0f,
            0.0f,
            width,
            2,
            32,
            2
        )
        RenderSystem.disableBlend()
    }

    private fun onDone() {
        client?.setScreen(parent)
    }

    private fun refreshScreen() {
        client?.setScreen(EditConfigScreen(parent))
    }

    @Environment(EnvType.CLIENT)
    class LabeledFieldWidget(
        textRenderer: TextRenderer,
        width: Int,
        height: Int,
        label: Text,
        initialText: String,
        onTextChanged: (String) -> Unit
    ) : DirectionalLayoutWidget(0, 0, DisplayAxis.VERTICAL) {
        val textField: TextFieldWidget = TextFieldWidget(textRenderer, width, height, label)

        init {
            textField.text = initialText
            textField.setChangedListener { newText -> onTextChanged(newText) }
            add(LayoutWidgets.createLabeledWidget(textRenderer, textField, label))
        }
    }

    @Environment(EnvType.CLIENT)
    class VerticalLabeledBlockPosWidget(
        textRenderer: TextRenderer,
        label: Text,
        pos: BlockPos?,
        onChanged: (x: Int?, y: Int?, z: Int?) -> Unit
    ) : DirectionalLayoutWidget(0, 0, DisplayAxis.VERTICAL) {
        val xField: LayoutWidget = createLabeledPosField(textRenderer, "X:", pos?.x) { onChanged(it, pos?.y, pos?.z) }
        val yField: LayoutWidget = createLabeledPosField(textRenderer, "Y:", pos?.y) { onChanged(pos?.x, it, pos?.z) }
        val zField: LayoutWidget = createLabeledPosField(textRenderer, "Z:", pos?.z) { onChanged(pos?.x, pos?.y, it) }
        val posField: DirectionalLayoutWidget = vertical().spacing(2)

        init {
            posField.add(xField)
            posField.add(yField)
            posField.add(zField)
            add(LayoutWidgets.createLabeledWidget(textRenderer, posField, label))
        }
    }

    @Environment(EnvType.CLIENT)
    class SwitchPosWidget(
        textRenderer: TextRenderer,
        label: Text,
        pos: BlockPos?,
        onChanged: (x: Int?, y: Int?, z: Int?) -> Unit,
        onRemove: () -> Unit
    ) : DirectionalLayoutWidget(0, 0, DisplayAxis.HORIZONTAL) {
        val xField: TextFieldWidget = createPosField(textRenderer, "X", pos?.x) { onChanged(it, pos?.y, pos?.z) }
        val yField: TextFieldWidget = createPosField(textRenderer, "Y", pos?.y) { onChanged(pos?.x, it, pos?.z) }
        val zField: TextFieldWidget = createPosField(textRenderer, "Z", pos?.z) { onChanged(pos?.x, pos?.y, it) }
        val removeButton: ButtonWidget = ButtonWidget.builder(
            Text.translatable("menu.rmscmod.editConfigScreen.removeSwitch")
        ) { onRemove() }.width(40).build()
        val posField: DirectionalLayoutWidget = horizontal().spacing(2)

        init {
            posField.add(xField)
            posField.add(yField)
            posField.add(zField)
            posField.add(removeButton)
            add(LayoutWidgets.createLabeledWidget(textRenderer, posField, label))
        }
    }
}