package top.voemp.rmscmod.selection

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.tag.ModBlockTags
import top.voemp.rmscmod.util.RenderUtils.drawBlockOutline
import top.voemp.rmscmod.util.RenderUtils.drawRegionBoxOutline

object SelectionManager {
    var isActive: Boolean = false
        private set
    var point1: BlockPos? = null
    var point2: BlockPos? = null
    var switchPosList: Set<BlockPos> = mutableSetOf()

    var lastLeftClickTime = 0L

    fun updateState(stack: ItemStack?) {
        isActive = stack?.item == Items.AMETHYST_SHARD
    }

    fun handleLeftClick(player: PlayerEntity, pos: BlockPos) {
        if (!isActive || (System.currentTimeMillis() - lastLeftClickTime) <= 150L) return
        lastLeftClickTime = System.currentTimeMillis()

        if (point1 != pos) {
            point1 = pos
            player.sendMessage(Text.literal("已选择点：§c${pos.x}, ${pos.y}, ${pos.z}"), true)
        } else {
            point1 = null
            player.sendMessage(Text.literal("已取消选择点：§c${pos.x}, ${pos.y}, ${pos.z}"), true)
        }
    }

    fun handleRightClick(player: PlayerEntity, world: World, pos: BlockPos) {
        if (!isActive) return

        when {
            Screen.hasControlDown() -> {
                if (world.getBlockState(pos).isIn(ModBlockTags.SWITCH_SELECTION)) {
                    if (!switchPosList.contains(pos)) {
                        switchPosList += pos
                        player.sendMessage(Text.literal("已选择开关：§e${pos.x}, ${pos.y}, ${pos.z}"), true)
                    } else {
                        switchPosList -= pos
                        player.sendMessage(Text.literal("已取消选择开关：§e${pos.x}, ${pos.y}, ${pos.z}"), true)
                    }
                } else {
                    player.sendMessage(Text.literal("§c该方块不能作为开关。"), true)
                }
            }

            else -> {
                if (point2 != pos) {
                    point2 = pos
                    player.sendMessage(Text.literal("已选择点：§9${pos.x}, ${pos.y}, ${pos.z}"), true)
                } else {
                    point2 = null
                    player.sendMessage(Text.literal("已取消选择点：§9${pos.x}, ${pos.y}, ${pos.z}"), true)
                }
            }
        }
    }

    fun renderHud(context: DrawContext) {
        if (!isActive) return

        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer
        val lines = mutableListOf<String>()

        val p1 = point1
        val p2 = point2

        if (!Screen.hasControlDown()) {
            lines.add("§f[§d框选模式§f] §a角点")

            if (p1 != null && p2 != null) {
                val dx = kotlin.math.abs(p1.x - p2.x) + 1
                val dy = kotlin.math.abs(p1.y - p2.y) + 1
                val dz = kotlin.math.abs(p1.z - p2.z) + 1
                lines.add("§f尺寸: §a$dx x $dy x $dz")
            }

            if (p1 != null) {
                lines.add("§f点1: §a${p1.x}, ${p1.y}, ${p1.z}")
            }

            if (p2 != null) {
                lines.add("§f点2: §a${p2.x}, ${p2.y}, ${p2.z}")
            }
        } else {
            lines.add("§f[§d框选模式§f] §a开关")
            if (switchPosList.isNotEmpty()) {
                lines.add("§f已选开关: §a${switchPosList.size}")
                switchPosList.forEach {
                    lines.add("§f开关${switchPosList.indexOf(it) + 1}: §a${it.x}, ${it.y}, ${it.z}")
                }
            }
        }

        if (!Screen.hasShiftDown()) {
            lines.add("§7按Shift键显示更多信息")
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

    fun renderPreviewWorld(context: WorldRenderContext) {
        if (!isActive) return

        val matrices = MatrixStack()
        val camera = context.camera()
        val provider = context.consumers() ?: return

        if (point1 != null && point2 != null) {
            drawRegionBoxOutline(point1!!, point2!!, 1f, 1f, 1f, matrices, camera, provider)
        }

        point1?.let {
            drawBlockOutline(it, 1f, 0f, 0f, matrices, camera, provider)
        }

        point2?.let {
            drawBlockOutline(it, 0f, 0.4f, 1f, matrices, camera, provider)
        }

        switchPosList.forEach {
            drawBlockOutline(it, 1f, 1f, 0f, matrices, camera, provider)
        }
    }

    fun hasAreaSelection(): Boolean = point1 != null && point2 != null

    fun hasSwitchSelection(): Boolean = switchPosList.isNotEmpty()

    fun clear() {
        point1 = null
        point2 = null
        switchPosList = mutableSetOf()
    }
}
