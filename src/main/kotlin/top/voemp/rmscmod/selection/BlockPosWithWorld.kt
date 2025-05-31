package top.voemp.rmscmod.selection

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

data class BlockPosWithWorld(
    var world: Identifier,
    var pos: BlockPos
) {
    fun setPos(newX: Int?, newY: Int?, newZ: Int?) {
        pos = BlockPos(newX ?: pos.x, newY ?: pos.y, newZ ?: pos.z)
    }
}