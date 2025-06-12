package top.voemp.rmscmod

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.gui.ModMenuScreen
import top.voemp.rmscmod.option.ModKeyBinding
import top.voemp.rmscmod.render.HudRender
import top.voemp.rmscmod.render.WorldRender
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.serial.FontBitmap
import top.voemp.rmscmod.tag.ModBlockTags

object RMSCModClient : ClientModInitializer {
    override fun onInitializeClient() {
        FontBitmap.init()  // 初始化字体

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
            if (!world.isClient) return@AttackBlockCallback ActionResult.PASS
            if (hand == Hand.MAIN_HAND && SelectionManager.isActive) {
                SelectionManager.handleLeftClick(player, world, pos)
                ActionResult.FAIL
            } else ActionResult.PASS
        })

        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            if (!world.isClient) return@UseBlockCallback ActionResult.PASS
            if (hand == Hand.MAIN_HAND && SelectionManager.isActive) {
                SelectionManager.handleRightClick(player, world, hitResult.blockPos)
                ActionResult.FAIL
            } else ActionResult.PASS
        })

        ServerWorldEvents.UNLOAD.register(ServerWorldEvents.Unload { server, world ->
            ConfigManager.resetConfig()
        })
    }
}