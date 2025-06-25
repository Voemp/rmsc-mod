package top.voemp.rmscmod.tag

import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import top.voemp.rmscmod.RMSCMod

object ModBlockTags {
    val SWITCH_SELECTION: TagKey<Block?>? = of("switch_selection")

    private fun of(id: String): TagKey<Block?>? {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(RMSCMod.MOD_ID, id))
    }

    fun registerModBlockTags() {}
}