package top.voemp.rmscmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.slf4j.LoggerFactory
import top.voemp.rmscmod.network.ModPayloads
import top.voemp.rmscmod.util.InventoryUtils

object RMSCMod : ModInitializer {
    const val MOD_ID = "rmscmod"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        ModPayloads.registerPayloads()

        ServerPlayNetworking.registerGlobalReceiver(ModPayloads.AreaSelectionC2SPayload.ID) { payload, context ->
            val items = InventoryUtils.getItemsFromInventory(context.server(), payload.areaSelection)
            val itemCounts = InventoryUtils.formatItemCounts(items)
            ServerPlayNetworking.send(context.player(), ModPayloads.ItemListS2CPayload(itemCounts))
        }
    }
}