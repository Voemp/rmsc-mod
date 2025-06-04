package top.voemp.rmscmod.util

import net.minecraft.block.ChestBlock
import net.minecraft.inventory.Inventory
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import top.voemp.rmscmod.selection.AreaPosWithWorld

object InventoryUtils {
    fun getInventory(world: World, pos: BlockPos): Inventory? {
        val state = world.getBlockState(pos)
        return if (state.block is ChestBlock) {
            ChestBlock.getInventory(state.block as ChestBlock, state, world, pos, false)
        } else {
            val be = world.getBlockEntity(pos)
            be as? Inventory
        }
    }

    fun getItemsFromInventory(server: MinecraftServer, area: AreaPosWithWorld): Map<String, Int> {
        val itemCounts = mutableMapOf<String, Int>()
        val world = server.getWorld(area.world)
        val pos1 = area.pos1!!
        val pos2 = area.pos2!!
        val minX = minOf(pos1.x, pos2.x)
        val minY = minOf(pos1.y, pos2.y)
        val minZ = minOf(pos1.z, pos2.z)
        val maxX = maxOf(pos1.x, pos2.x)
        val maxY = maxOf(pos1.y, pos2.y)
        val maxZ = maxOf(pos1.z, pos2.z)

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val currentPos = BlockPos(x, y, z)
                    val blockEntity = world?.getBlockEntity(currentPos)
                    if (blockEntity !is Inventory) continue
                    for (i in 0 until blockEntity.size()) {
                        val stack = blockEntity.getStack(i)
                        if (!stack.isEmpty) {
                            val itemName = stack.item.name.string
                            itemCounts[itemName] = itemCounts.getOrDefault(itemName, 0) + stack.count
                        }
                    }
                }
            }
        }

        return itemCounts
    }

    fun formatItemCounts(itemCounts: Map<String, Int>): List<String> {
        return itemCounts.map { (itemName, count) ->
            val formattedCount = when {
                count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
                else -> count.toString()
            }
            "$itemName $formattedCount"
        }
    }
}