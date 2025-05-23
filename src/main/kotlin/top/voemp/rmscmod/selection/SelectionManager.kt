package top.voemp.rmscmod.selection

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.voemp.rmscmod.tag.ModBlockTags

object SelectionManager {
    var isActive: Boolean = false
        private set
    var point1: BlockPos? = null
    var point2: BlockPos? = null
    var areaSelectionWorld: RegistryKey<World>? = null
    var switchSet: MutableSet<BlockPosWithWorld> = mutableSetOf()

    var lastLeftClickTime = 0L

    fun updateState(stack: ItemStack?) {
        isActive = stack?.item == Items.AMETHYST_SHARD
    }

    fun addSwitch(switchPos: BlockPosWithWorld) {
        switchSet += switchPos
    }

    fun removeSwitch(switchPos: BlockPosWithWorld) {
        switchSet -= switchPos
    }

    fun handleLeftClick(player: PlayerEntity, world: World, pos: BlockPos) {
        if (!isActive || (System.currentTimeMillis() - lastLeftClickTime) <= 150L) return
        lastLeftClickTime = System.currentTimeMillis()

        if (point1 != pos) {
            if (areaSelectionWorld != null && areaSelectionWorld != world.registryKey) {
                player.sendMessage(Text.literal("§c两个选区点必须在同一维度！"), true)
                return
            }
            point1 = pos
            areaSelectionWorld = world.registryKey
            player.sendMessage(Text.literal("已选择点：§c${pos.x}, ${pos.y}, ${pos.z}"), true)
        } else {
            point1 = null
            if (point2 == null) areaSelectionWorld = null
            player.sendMessage(Text.literal("已取消选择点：§c${pos.x}, ${pos.y}, ${pos.z}"), true)
        }
    }

    fun handleRightClick(player: PlayerEntity, world: World, pos: BlockPos) {
        if (!isActive) return

        when {
            Screen.hasControlDown() -> {
                if (!world.getBlockState(pos).isIn(ModBlockTags.SWITCH_SELECTION)) {
                    player.sendMessage(Text.literal("§c该方块不能作为开关"), true)
                    return
                }
                val switchPos = BlockPosWithWorld(world.registryKey.value, pos)
                if (!switchSet.contains(switchPos)) {
                    if (switchSet.size >= 4) {
                        player.sendMessage(Text.literal("§c开关数量超过上限"), true)
                        return
                    }
                    addSwitch(switchPos)
                    player.sendMessage(Text.literal("已选择开关：§e${pos.x}, ${pos.y}, ${pos.z}"), true)
                } else {
                    removeSwitch(switchPos)
                    player.sendMessage(Text.literal("已取消选择开关：§e${pos.x}, ${pos.y}, ${pos.z}"), true)
                }
            }

            else -> {
                if (point2 != pos) {
                    if (areaSelectionWorld != null && areaSelectionWorld != world.registryKey) {
                        player.sendMessage(Text.literal("§c两个选区点必须在同一维度！"), true)
                        return
                    }
                    point2 = pos
                    areaSelectionWorld = world.registryKey
                    player.sendMessage(Text.literal("已选择点：§9${pos.x}, ${pos.y}, ${pos.z}"), true)
                } else {
                    point2 = null
                    if (point1 == null) areaSelectionWorld = null
                    player.sendMessage(Text.literal("已取消选择点：§9${pos.x}, ${pos.y}, ${pos.z}"), true)
                }
            }
        }
    }

    fun hasAreaSelection(): Boolean = point1 != null && point2 != null

    fun hasSwitchSelection(): Boolean = switchSet.isNotEmpty()

    fun clearAreaSelection() {
        point1 = null
        point2 = null
        areaSelectionWorld = null
    }

    fun clearSwitchSelection() {
        switchSet = mutableSetOf()
    }

    fun clearAll() {
        clearAreaSelection()
        clearSwitchSelection()
    }
}
