package top.voemp.rmscmod.network

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import top.voemp.rmscmod.RMSCMod.MOD_ID
import top.voemp.rmscmod.selection.AreaPosWithWorld
import top.voemp.rmscmod.selection.BlockPosWithWorld

object ModPayloads {
    data class AreaSelectionC2SPayload(val areaSelection: AreaPosWithWorld) : CustomPayload {
        companion object {
            private val AREA_SELECTION_PACKET_ID: Identifier = Identifier.of(MOD_ID, "area_selection")
            val ID = CustomPayload.Id<AreaSelectionC2SPayload>(AREA_SELECTION_PACKET_ID)
            val CODEC: PacketCodec<RegistryByteBuf, AreaSelectionC2SPayload> =
                PacketCodec.tuple(
                    AreaPosWithWorld.PACKET_CODEC,
                    { it.areaSelection },
                    ::AreaSelectionC2SPayload
                )
        }

        override fun getId(): CustomPayload.Id<out CustomPayload> = ID
    }

    data class ItemListS2CPayload(val itemList: List<String>) : CustomPayload {
        companion object {
            private val ITEM_LIST_PACKET_ID: Identifier = Identifier.of(MOD_ID, "item_list")
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

    data class SwitchListC2SPayload(val switchList: List<BlockPosWithWorld>) : CustomPayload {
        companion object {
            private val SWITCH_SET_PACKET_ID: Identifier = Identifier.of(MOD_ID, "switch_list")
            val ID = CustomPayload.Id<SwitchListC2SPayload>(SWITCH_SET_PACKET_ID)
            val CODEC: PacketCodec<RegistryByteBuf, SwitchListC2SPayload> =
                PacketCodec.tuple(
                    PacketCodecs.collection({ size -> ArrayList(size) }, BlockPosWithWorld.PACKET_CODEC),
                    { it.switchList },
                    ::SwitchListC2SPayload
                )
        }

        override fun getId(): CustomPayload.Id<out CustomPayload> = ID
    }

    data class SwitchStatusS2CPayload(val switchStatus: Boolean) : CustomPayload {
        companion object {
            private val SWITCH_STATUS_PACKET_ID: Identifier = Identifier.of(MOD_ID, "switch_status")
            val ID = CustomPayload.Id<SwitchStatusS2CPayload>(SWITCH_STATUS_PACKET_ID)
            val CODEC: PacketCodec<RegistryByteBuf, SwitchStatusS2CPayload> =
                PacketCodec.tuple(
                    PacketCodecs.BOOL,
                    { it.switchStatus },
                    ::SwitchStatusS2CPayload
                )
        }

        override fun getId(): CustomPayload.Id<out CustomPayload> = ID
    }

    fun registerPayloads() {
        PayloadTypeRegistry.playC2S().register(AreaSelectionC2SPayload.ID, AreaSelectionC2SPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(ItemListS2CPayload.ID, ItemListS2CPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(SwitchListC2SPayload.ID, SwitchListC2SPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(SwitchStatusS2CPayload.ID, SwitchStatusS2CPayload.CODEC)
    }
}