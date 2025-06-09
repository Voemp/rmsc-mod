package top.voemp.rmscmod.serial

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.network.ModPayloads

object DataManager {
    private var configData: List<String>? = null
    private var inventoryData: List<String>? = null

    fun getConfigData() {}

    fun getInventoryData(name: String) {
        val config = ConfigManager.loadConfig(name)
        if (config?.areaSelection == null) return

        ClientPlayNetworking.send(ModPayloads.AreaSelectionC2SPayload(config.areaSelection))
        ClientPlayNetworking.registerGlobalReceiver(ModPayloads.ItemListS2CPayload.ID) { payload, context ->
            if (context.client().world == null) return@registerGlobalReceiver
            inventoryData = payload.itemList
        }
    }
}