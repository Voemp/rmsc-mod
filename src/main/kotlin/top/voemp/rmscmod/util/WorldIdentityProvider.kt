package top.voemp.rmscmod.util

import net.minecraft.client.MinecraftClient

object WorldIdentityProvider {
    fun getWorldIdentifier(): String {
        val client = MinecraftClient.getInstance()

        return if (client.isIntegratedServerRunning) {
            client.server?.saveProperties?.levelName ?: "unknown_sp"
        } else {
            client.currentServerEntry?.address?.replace(":", "_") ?: "unknown_mp"
        }
    }
}
