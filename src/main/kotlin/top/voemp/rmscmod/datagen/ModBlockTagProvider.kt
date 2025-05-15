package top.voemp.rmscmod.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Blocks
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import top.voemp.rmscmod.tag.ModBlockTags
import java.util.concurrent.CompletableFuture

class ModBlockTagProvider : FabricTagProvider.BlockTagProvider {
    constructor(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : super(
        dataOutput,
        registryLookup
    )

    override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
        getOrCreateTagBuilder(ModBlockTags.SWITCH_SELECTION)
            .forceAddTag(BlockTags.BUTTONS)
            .add(Blocks.LEVER)
            .add(Blocks.NOTE_BLOCK)
    }
}