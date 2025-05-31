package top.voemp.rmscmod.util

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.*
import net.minecraft.text.Text

object GuiUtils {
    fun createLabeledPosField(
        textRenderer: TextRenderer,
        label: String,
        value: Int?,
        onChange: (Int) -> Unit
    ): LayoutWidget {
        val field = createPosField(textRenderer, label, value, onChange)
        return createHorizontalLabeledWidget(textRenderer, field, Text.literal(label))
    }

    fun createPosField(
        textRenderer: TextRenderer,
        label: String,
        value: Int?,
        onChange: (Int) -> Unit
    ): TextFieldWidget {
        val field = TextFieldWidget(textRenderer, 45, 20, Text.literal(label))
        field.text = value?.toString() ?: ""
        field.setChangedListener { text ->
            text.toIntOrNull()?.let { onChange(it) }
        }
        return field
    }

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