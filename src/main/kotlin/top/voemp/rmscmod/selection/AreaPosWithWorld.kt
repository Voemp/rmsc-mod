package top.voemp.rmscmod.selection

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class AreaPosWithWorld(
    var world: RegistryKey<World>?,
    var pos1: BlockPos?,
    var pos2: BlockPos?
) {
    companion object {
        val PACKET_CODEC: PacketCodec<ByteBuf?, AreaPosWithWorld?> = PacketCodec.tuple(
            PacketCodecs.codec(RegistryKey.createCodec(RegistryKeys.WORLD)), AreaPosWithWorld::world,
            BlockPos.PACKET_CODEC, AreaPosWithWorld::pos1,
            BlockPos.PACKET_CODEC, AreaPosWithWorld::pos2,
            ::AreaPosWithWorld
        )
    }

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
}
