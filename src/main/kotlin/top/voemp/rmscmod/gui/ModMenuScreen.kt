package top.voemp.rmscmod.gui

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.selection.SelectionManager

@Environment(EnvType.CLIENT)
class ModMenuScreen : Screen(Text.translatable("rmscmod.menu.title")) {
    private lateinit var nameField: TextFieldWidget
    private lateinit var saveButton: ButtonWidget
    private lateinit var cancelButton: ButtonWidget

    override fun init() {
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
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 60, 0xFFFFFF)
        if (SelectionManager.hasAreaSelection()) {
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("当前选中区域：${SelectionManager.point1} -> ${SelectionManager.point2}"),
                width / 2,
                height / 2 - 45,
                0xAAAAAA
            )
        } else if (SelectionManager.hasSwitchSelection()) {
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("当前选中拉杆：${SelectionManager.switchPosSet}"),
                width / 2,
                height / 2 - 45,
                0xAAAAAA
            )
        }
        super.render(context, mouseX, mouseY, delta)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (ModKeyBinding.OPEN_MENU.matchesKey(keyCode, scanCode)) {
            MinecraftClient.getInstance().setScreen(null)
            return true
        }
        return nameField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return nameField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button)
    }
}