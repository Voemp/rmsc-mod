package top.voemp.rmscmod.selection

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.voemp.rmscmod.tag.ModBlockTags

object SelectionManager {
    var isActive: Boolean = false
        private set
    var point1: BlockPos? = null
    var point2: BlockPos? = null
    var switchPosSet: Set<BlockPos> = mutableSetOf()

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
                    if (!switchPosSet.contains(pos)) {
                        switchPosSet += pos
                        player.sendMessage(Text.literal("已选择开关：§e${pos.x}, ${pos.y}, ${pos.z}"), true)
                    } else {
                        switchPosSet -= pos
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

    fun hasAreaSelection(): Boolean = point1 != null && point2 != null

    fun hasSwitchSelection(): Boolean = switchPosSet.isNotEmpty()

    fun clearAreaSelection() {
        point1 = null
        point2 = null
    }

    fun clearSwitchSelection() {
        switchPosSet = mutableSetOf()
    }

    fun clearAll() {
        clearAreaSelection()
        clearSwitchSelection()
    }
}
