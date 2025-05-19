package top.voemp.rmscmod.util

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.*
import net.minecraft.text.Text

object GuiUtils {
    fun createHorizontalLabeledWidget(
        textRenderer: TextRenderer,
        widget: Widget,
        label: Text
    ): LayoutWidget {
        val directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4)
        directionalLayoutWidget.add(TextWidget(textRenderer.getWidth(label), widget.height, label, textRenderer))
        directionalLayoutWidget.add(widget)
        return directionalLayoutWidget
    }
}