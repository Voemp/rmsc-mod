package top.voemp.rmscmod.selection

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.voemp.rmscmod.gui.ModMenuScreen
import top.voemp.rmscmod.tag.ModBlockTags

object SelectionManager {
    var isActive: Boolean = false
        private set
    var pointA: BlockPos? = null
    var pointB: BlockPos? = null
    var switchPosList: List<BlockPos?>? = mutableListOf()

    fun updateState(stack: ItemStack?) {
        isActive = stack?.item == Items.AMETHYST_SHARD
    }

    fun handleLeftClick(player: PlayerEntity, pos: BlockPos) {
        if (!isActive) return

        pointA = pos
        player.sendMessage(Text.literal("已选择一个点：$pos"), true)
    }

    fun handleRightClick(player: PlayerEntity, world: World, pos: BlockPos) {
        if (!isActive) return

        when {
            Screen.hasAltDown() -> {
                MinecraftClient.getInstance().setScreen(ModMenuScreen)
            }

            Screen.hasControlDown() -> {
                if (world.getBlockState(pos).isIn(ModBlockTags.SWITCH_SELECTION)) {
                    switchPosList = switchPosList?.plus(pos)
                    player.sendMessage(Text.literal("§a已选择开关：$pos"), true)
                } else {
                    player.sendMessage(Text.literal("§c该方块不能作为开关。"), true)
                }
            }

            else -> {
                pointB = pos
                player.sendMessage(Text.literal("已选择一个点：$pos"), true)
            }
        }
    }

    fun renderHud(context: DrawContext) {
        if (!isActive) return

        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer
        val lines = mutableListOf<String>()

        lines.add("§f[§d框选模式§f] §a角点")

        val a = pointA
        val b = pointB

        if (a != null && b != null) {
            val dx = kotlin.math.abs(a.x - b.x) + 1
            val dy = kotlin.math.abs(a.y - b.y) + 1
            val dz = kotlin.math.abs(a.z - b.z) + 1
            lines.add("§f尺寸: §a${dx} x ${dy} x ${dz}")
        }

        if (a != null) {
            lines.add("§f点1: §a${a.x}, ${a.y}, ${a.z}")
        }

        if (b != null) {
            lines.add("§f点2: §a${b.x}, ${b.y}, ${b.z}")
        }

        lines.add("§7左键/右键选点")
        lines.add("§7Ctrl + 右键选择开关")
        lines.add("§7Alt + 右键键打开菜单")

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
            context.drawText(textRenderer, line, x, y + index * lineHeight, 0xFFFFFF, false)
        }
    }


    fun hasAreaSelection(): Boolean = pointA != null && pointB != null

    fun hasSwitchSelection(): Boolean = switchPosList != null && switchPosList!!.isNotEmpty()

    fun clear() {
        pointA = null
        pointB = null
        switchPosList = mutableListOf()
    }
}
