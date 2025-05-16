package top.voemp.rmscmod.option

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object ModKeyBinding {
    val OPEN_MENU: KeyBinding = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "key.rmscmod.open_menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_PERIOD,
            "category.rmscmod"
        )
    )

    fun registerKeyBindings() {}
}
