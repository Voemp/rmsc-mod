package top.voemp.rmscmod.gui.component

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.DirectionalLayoutWidget
import net.minecraft.client.gui.widget.LayoutWidgets
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

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