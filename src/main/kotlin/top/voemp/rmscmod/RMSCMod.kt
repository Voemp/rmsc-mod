package top.voemp.rmscmod

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import org.slf4j.LoggerFactory
import top.voemp.rmscmod.gui.ModMenuScreen
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.render.HudRender
import top.voemp.rmscmod.render.WorldRender
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.tag.ModBlockTags

object RMSCMod : ModInitializer {
    const val MOD_ID = "rmscmod"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        logger.info("Hello Fabric world!")

        ModBlockTags.registerModBlockTags()
        ModKeyBinding.registerKeyBindings()

        HudRender.registerHudEvents()
        WorldRender.registerWorldRenderEvents()

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            SelectionManager.updateState(client.player?.mainHandStack)
            if (ModKeyBinding.OPEN_MENU.wasPressed() && client.player != null) {
                client.setScreen(ModMenuScreen())
            }
        })

        AttackBlockCallback.EVENT.register(AttackBlockCallback { player, world, hand, pos, _ ->
            if (world.isClient && hand == Hand.MAIN_HAND && SelectionManager.isActive) {
                SelectionManager.handleLeftClick(player, world, pos)
                ActionResult.FAIL
            } else ActionResult.PASS
        })

        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            if (world.isClient && hand == Hand.MAIN_HAND && SelectionManager.isActive) {
                SelectionManager.handleRightClick(player, world, hitResult.blockPos)
                ActionResult.FAIL
            } else ActionResult.PASS
        })
    }
}