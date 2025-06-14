package top.voemp.rmscmod.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.GameOptionsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.gui.widget.GridWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import top.voemp.rmscmod.gui.component.LabeledFieldWidget
import top.voemp.rmscmod.serial.DataManager
import top.voemp.rmscmod.serial.SerialManager

class SerialScreen(parent: Screen?) :
    GameOptionsScreen(parent, null, Text.translatable("menu.rmscmod.serialScreen.title")) {
    companion object {
        private val ENTER_PORT_DESCRIPTOR = Text.translatable("menu.rmscmod.serialScreen.enterPortDescriptor")
        private val ENTER_BAUD_RATE = Text.translatable("menu.rmscmod.serialScreen.enterBaudRate")
    }

    private var connectButton: ButtonWidget? = null
    private var disconnectButton: ButtonWidget? = null

    override fun initBody() {
        val grid = layout.addBody(GridWidget()).setRowSpacing(8).createAdder(1)
        val portDescriptorField = LabeledFieldWidget(
            textRenderer,
            120,
            20,
            ENTER_PORT_DESCRIPTOR,
            SerialManager.serialConfig.portDescriptor
        ) {
            SerialManager.serialConfig.portDescriptor = it
            refreshButtonActive()
        }
        val baudRateField = LabeledFieldWidget(
            textRenderer,
            120,
            20,
            ENTER_BAUD_RATE,
            SerialManager.serialConfig.baudRate.toString()
        ) {
            val baudRate = it.toIntOrNull() ?: 0
            SerialManager.serialConfig.baudRate = baudRate
            refreshButtonActive()
        }
        grid.add(portDescriptorField)
        grid.add(baudRateField)
    }

    override fun initFooter() {
        val footerWidget = layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8))
        footerWidget.mainPositioner.alignHorizontalCenter()
        connectButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.serialScreen.connect")
            ) { onConnect() }.width(100).build()
        )
        disconnectButton = footerWidget.add(
            ButtonWidget.builder(
                Text.translatable("menu.rmscmod.serialScreen.disconnect")
            ) { onDisconnect() }.width(100).build()
        )
        footerWidget.add(
            ButtonWidget.builder(
                ScreenTexts.DONE
            ) { onDone() }.width(100).build()
        )
        refreshButtonActive()
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

    override fun close() {
        super.close()
        if (SerialManager.serialConfig != SerialManager.lastSerialConfig) SerialManager.saveConfig()
    }

    private fun onConnect() {
        if (client == null || !SerialManager.openPort()) return
        DataManager.init()
        while (SerialManager.isAvailable()) SerialManager.read()
        SerialManager.startSerialListener()
        refreshScreen()
    }

    private fun onDisconnect() {
        SerialManager.closePort()
        refreshScreen()
    }

    private fun onDone() {
        close()
    }

    private fun refreshButtonActive() {
        connectButton?.active = SerialManager.hasConfig() && !SerialManager.isConnected()
        disconnectButton?.active = SerialManager.isConnected()
    }

    fun refreshScreen() {
        client?.setScreen(SerialScreen(parent))
    }
}