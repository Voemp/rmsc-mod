package top.voemp.rmscmod.selection

import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

data class AreaPosWithWorld(
    var world: Identifier?,
    var pos1: BlockPos?,
    var pos2: BlockPos?
) {
    fun setPos1(newX: Int?, newY: Int?, newZ: Int?) {
        pos1 = pos1?.let {
             BlockPos(newX ?: it.x, newY ?: it.y, newZ ?: it.z)
        }
    }

    fun setPos2(newX: Int?, newY: Int?, newZ: Int?) {
        pos2 = pos2?.let {
            BlockPos(newX ?: it.x, newY ?: it.y, newZ ?: it.z)
        }
    }

    fun setPos1X(newX: Int) {
        pos1 = pos1?.let {
            BlockPos(newX, it.y, it.z)
        }
    }

    fun setPos1Y(newY: Int) {
        pos1 = pos1?.let {
            BlockPos(it.x, newY, it.z)
        }
    }

    fun setPos1Z(newZ: Int) {
        pos1 = pos1?.let {
            BlockPos(it.x, it.y, newZ)
        }
    }

    fun setPos2X(newX: Int) {
        pos2 = pos2?.let {
            BlockPos(newX, it.y, it.z)
        }
    }

    fun setPos2Y(newY: Int) {
        pos2 = pos2?.let {
            BlockPos(it.x, newY, it.z)
        }
    }

    fun setPos2Z(newZ: Int) {
        pos2 = pos2?.let {
            BlockPos(it.x, it.y, newZ)
        }
    }
}
