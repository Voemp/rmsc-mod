package top.voemp.rmscmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.ActionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.voemp.rmscmod.network.ModPayloads
import top.voemp.rmscmod.util.InteractionUtils
import top.voemp.rmscmod.util.InventoryUtils

object RMSCMod : ModInitializer {
    const val MOD_ID = "rmscmod"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        ModPayloads.registerPayloads()

        ServerPlayNetworking.registerGlobalReceiver(ModPayloads.AreaSelectionC2SPayload.ID) { payload, context ->
            context.server().execute {
                val items = InventoryUtils.getItemsFromInventory(context.server(), payload.areaSelection)
                val itemCounts = InventoryUtils.formatItemCounts(items)
                ServerPlayNetworking.send(context.player(), ModPayloads.ItemListS2CPayload(itemCounts))
            }
        }

        ServerPlayNetworking.registerGlobalReceiver(ModPayloads.SwitchListC2SPayload.ID) { payload, context ->
            context.server().execute {
                var changeResult = true
                payload.switchList.forEach { switch ->
                    val result = InteractionUtils.changeSwitch(context.server(), switch)
                    changeResult = result == ActionResult.CONSUME && changeResult
                }
                ServerPlayNetworking.send(context.player(), ModPayloads.SwitchChangeResultS2CPayload(changeResult))
            }
        }
    }
}