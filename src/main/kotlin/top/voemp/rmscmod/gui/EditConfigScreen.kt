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
import top.voemp.rmscmod.gui.component.LabeledFieldWidget
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.util.GuiUtils.createLabeledPosField
import top.voemp.rmscmod.util.GuiUtils.createPosField

@Environment(EnvType.CLIENT)
class EditConfigScreen(parent: Screen?) :
    GameOptionsScreen(parent, null, Text.translatable("menu.rmscmod.editConfigScreen.title")) {
    companion object {
        private val ENTER_CONFIG_NAME: Text = Text.translatable("menu.rmscmod.editConfigScreen.enterConfigName")
    }

    override fun initBody() {
        val grid = layout.addBody(GridWidget()).setColumnSpacing(32).createAdder(2)
        val leftColumn = grid.add(DirectionalLayoutWidget.vertical().spacing(8))
        val rightColumn = grid.add(DirectionalLayoutWidget.vertical().spacing(16))
        val configNameField = LabeledFieldWidget(
            textRenderer,
            140,
            20,
            ENTER_CONFIG_NAME,
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
                SelectionManager.areaSelection.world?.value ?: "None"
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
                    Text.translatable(
                        "menu.rmscmod.editConfigScreen.switchPos",
                        index + 1,
                        switch.world.value
                    ),
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
                TextWidget(140, 160, Text.translatable("menu.rmscmod.editConfigScreen.noSwitch"), textRenderer)
            )
        }
    }

    override fun initFooter() {
        val footerWidget: DirectionalLayoutWidget =
            layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        val saveButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfigScreen.saveConfig")
            ) { onSave() }.width(100).build()
        )
        saveButton.active = SelectionManager.hasAreaSelection() || SelectionManager.hasSwitchSelection()
        if (!saveButton.active) {
            saveButton.tooltip = Tooltip.of(Text.translatable("menu.rmscmod.editConfigScreen.saveConfig.tooltip"))
        }
        val cancelButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.editConfigScreen.cancelEdit")
            ) { onCancelEdit() }.width(100).build()
        )
        cancelButton.active = ConfigManager.getConfigId() != null
        if (!cancelButton.active) {
            cancelButton.tooltip = Tooltip.of(Text.translatable("menu.rmscmod.editConfigScreen.cancelEdit.tooltip"))
        }
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { onDone() }.width(100).build()
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

    private fun onSave() {
        val config = ConfigManager.build()
        val confirmTitleText: Text?
        val confirmMessageText: Text?
        if (ConfigManager.isExistConfig(config.id)) {
            confirmTitleText = Text.translatable("menu.rmscmod.editConfigScreen.confirmOverride")
            confirmMessageText = Text.translatable("menu.rmscmod.editConfigScreen.confirmOverride.message")
        } else {
            confirmTitleText = Text.translatable("menu.rmscmod.editConfigScreen.confirmSave")
            confirmMessageText = Text.translatable("menu.rmscmod.editConfigScreen.confirmSave.message")
        }
        val confirmScreen = ConfirmScreen(
            { confirmed ->
                if (confirmed) ConfigManager.saveConfig(config)
                refreshScreen()
            },
            confirmTitleText,
            confirmMessageText
        )
        client?.setScreen(confirmScreen)
    }

    private fun onCancelEdit() {
        ConfigManager.resetConfig()
        refreshScreen()
    }

    private fun onDone() {
        if (parent is ConfigListScreen) (parent as ConfigListScreen).refreshScreen()
        else client?.setScreen(parent)
    }

    private fun refreshScreen() {
        client?.setScreen(EditConfigScreen(parent))
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