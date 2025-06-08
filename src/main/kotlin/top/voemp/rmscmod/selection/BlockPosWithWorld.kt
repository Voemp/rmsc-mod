package top.voemp.rmscmod.selection

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class BlockPosWithWorld(
    var world: RegistryKey<World>,
    var pos: BlockPos
) {
    companion object {
        val PACKET_CODEC: PacketCodec<ByteBuf?, BlockPosWithWorld?> = PacketCodec.tuple(
            PacketCodecs.codec(RegistryKey.createCodec(RegistryKeys.WORLD)), BlockPosWithWorld::world,
            BlockPos.PACKET_CODEC, BlockPosWithWorld::pos,
            ::BlockPosWithWorld
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPosWithWorld) return false
        return world.registry == other.world.registry && world.value == other.world.value && pos == other.pos
    }

    override fun hashCode(): Int {
        return 31 * world.registry.hashCode() + 31 * world.value.hashCode() + pos.hashCode()
    }

    fun setPos(newX: Int?, newY: Int?, newZ: Int?) {
        pos = BlockPos(newX ?: pos.x, newY ?: pos.y, newZ ?: pos.z)
    }
}