package top.voemp.rmscmod.selection

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

data class BlockPosWithWorld(
    var world: Identifier,
    var pos: BlockPos
) {
    fun setWorld(newWorldIdentifier: Identifier): BlockPosWithWorld {
        this.world = newWorldIdentifier
        return this
    }

    fun setX(newX: Int): BlockPosWithWorld {
        this.pos = BlockPos(newX, this.pos.y, this.pos.z)
        return this
    }

    fun setY(newY: Int): BlockPosWithWorld {
        this.pos = BlockPos(this.pos.x, newY, this.pos.z)
        return this
    }

    fun setZ(newZ: Int): BlockPosWithWorld {
        this.pos = BlockPos(this.pos.x, this.pos.y, newZ)
        return this
    }
}