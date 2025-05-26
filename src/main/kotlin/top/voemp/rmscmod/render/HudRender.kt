package top.voemp.rmscmod.render

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.selection.SelectionManager
import kotlin.math.abs

object HudRender {
    val client: MinecraftClient = MinecraftClient.getInstance()

    fun renderSelectionHud(context: DrawContext) {
        if (!SelectionManager.isActive) return

        val textRenderer: TextRenderer = client.textRenderer
        val lines = mutableListOf<String>()

        val p1 = SelectionManager.areaSelection.pos1
        val p2 = SelectionManager.areaSelection.pos2
        val switchSet = SelectionManager.switchSet

        if (!Screen.hasControlDown()) {
            lines.add("§f[§d框选模式§f] §a角点")
            if (p1 != null && p2 != null) {
                val dx = abs(p1.x - p2.x) + 1
                val dy = abs(p1.y - p2.y) + 1
                val dz = abs(p2.z - p2.z) + 1
                lines.add("§f尺寸: §a$dx x $dy x $dz")
            }
            p1?.let {
                lines.add("§f点1: §a${p1.x}, ${p1.y}, ${p1.z}")
            }
            p2?.let {
                lines.add("§f点2: §a${p2.x}, ${p2.y}, ${p2.z}")
            }
        } else {
            lines.add("§f[§d框选模式§f] §a开关")
            if (switchSet.isNotEmpty()) {
                lines.add("§f已选开关: §a${switchSet.size}")
                switchSet.forEachIndexed { index, switch ->
                    lines.add("§f开关${index + 1}: §a${switch.pos.x}, ${switch.pos.y}, ${switch.pos.z}")
                }
            }
        }

        if (!Screen.hasShiftDown()) {
            lines.add("§7按住Shift显示更多信息")
        } else {
            lines.add("§7左键 / 右键选点")
            lines.add("§7Ctrl + 右键选择开关")
            lines.add("§7${ModKeyBinding.OPEN_MENU.boundKeyLocalizedText.literalString} 键打开菜单")
        }

        // 渲染背景和文本
        val padding = 4
        val lineHeight = textRenderer.fontHeight + 2
        val totalHeight = lines.size * lineHeight + padding * 2
        val maxWidth = lines.maxOf { textRenderer.getWidth(it) } + padding * 2

        val x = 10
        val y = client.window.scaledHeight - totalHeight - 10

        // 绘制背景
        context.fill(
            x - padding,
            y - padding,
            x + maxWidth - padding,
            y + totalHeight - padding,
            0xAA000000.toInt()
        )

        // 绘制每行文本
        lines.forEachIndexed { index, line ->
            context.drawText(textRenderer, line, x, y + index * lineHeight, 0xFFFFFF, true)
        }
    }

    fun registerHudEvents() {
        HudRenderCallback.EVENT.register(HudRenderCallback { context, _ ->
            renderSelectionHud(context)
        })
    }
}