package top.voemp.rmscmod.network

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import top.voemp.rmscmod.RMSCMod
import top.voemp.rmscmod.selection.AreaPosWithWorld

object ModPayloads {
    data class AreaSelectionC2SPayload(val areaSelection: AreaPosWithWorld) : CustomPayload {
        companion object {
            private val AREA_SELECTION_PACKET_ID: Identifier = Identifier.of(RMSCMod.MOD_ID, "area_selection")
            val ID = CustomPayload.Id<AreaSelectionC2SPayload>(AREA_SELECTION_PACKET_ID)
            val CODEC: PacketCodec<RegistryByteBuf, AreaSelectionC2SPayload> =
                PacketCodec.tuple(AreaPosWithWorld.PACKET_CODEC, { it.areaSelection }, ::AreaSelectionC2SPayload)
        }

        override fun getId(): CustomPayload.Id<out CustomPayload> = ID
    }

    data class ItemListS2CPayload(val itemList: List<String>) : CustomPayload {
        companion object {
            private val ITEM_LIST_PACKET_ID: Identifier = Identifier.of(RMSCMod.MOD_ID, "item_list")
            val ID = CustomPayload.Id<ItemListS2CPayload>(ITEM_LIST_PACKET_ID)
            val CODEC: PacketCodec<RegistryByteBuf, ItemListS2CPayload> =
                PacketCodec.tuple(
                    PacketCodecs.STRING.collect(PacketCodecs.toList()),
                    { it.itemList },
                    ::ItemListS2CPayload
                )
        }

        override fun getId(): CustomPayload.Id<out CustomPayload> = ID
    }

    fun registerPayloads() {
        PayloadTypeRegistry.playC2S().register(AreaSelectionC2SPayload.ID, AreaSelectionC2SPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(ItemListS2CPayload.ID, ItemListS2CPayload.CODEC)
    }
}