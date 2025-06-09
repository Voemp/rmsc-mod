package top.voemp.rmscmod.gui

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import top.voemp.rmscmod.option.ModKeyBinding

@Environment(EnvType.CLIENT)
class ModMenuScreen : GameOptionsScreen(null, null, Text.translatable("menu.rmscmod.title")) {
    override fun initBody() {
        val grid = layout.addBody(GridWidget()).setRowSpacing(16).setColumnSpacing(32).createAdder(2)
        grid.add(
            ButtonWidget.builder(Text.translatable("menu.rmscmod.editConfigScreen.title")) { button ->
                client?.setScreen(
                    EditConfigScreen(this)
                )
            }.build()
        )
        grid.add(
            ButtonWidget.builder(Text.translatable("menu.rmscmod.configListScreen.title")) { button ->
                client?.setScreen(
                    ConfigListScreen(this)
                )
            }.build()
        )
        grid.add(
            ButtonWidget.builder(Text.translatable("menu.rmscmod.serialScreen.title")) { button ->
                client?.setScreen(
                    SerialScreen(this)
                )
            }.build()
        )
    }

    override fun initFooter() {
        val footerWidget = layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        footerWidget.mainPositioner.alignHorizontalCenter()
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { button -> onDone() }.build()
        )
    }

    override fun addOptions() {}

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (ModKeyBinding.OPEN_MENU.matchesKey(keyCode, scanCode)) {
            client?.setScreen(null)
            return true
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    private fun onDone() {
        client?.setScreen(null)
    }
}