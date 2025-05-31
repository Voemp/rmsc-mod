package top.voemp.rmscmod.util

import net.minecraft.client.MinecraftClient

object LevelIdentityProvider {
    fun getLevelIdentifier(): String {
        val client = MinecraftClient.getInstance()

        return if (client.isIntegratedServerRunning) {
            client.server?.saveProperties?.levelName ?: "unknown_sp"
        } else {
            client.currentServerEntry?.address?.replace(":", "_") ?: "unknown_mp"
        }
    }
}
