package top.voemp.rmscmod.selection

import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class BlockPosWithWorld(
    var world: RegistryKey<World>,
    var pos: BlockPos
) {
    fun setPos(newX: Int?, newY: Int?, newZ: Int?) {
        pos = BlockPos(newX ?: pos.x, newY ?: pos.y, newZ ?: pos.z)
    }
}