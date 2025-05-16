package top.voemp.rmscmod.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class ModENUSLanguageProvider : FabricLanguageProvider {
    constructor(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : super(
        dataOutput,
        "en_us",
        registryLookup
    )

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup?,
        translationBuilder: TranslationBuilder?
    ) {
        translationBuilder?.add("gui.rmscmod.menu.title", "RMSC Menu")
        translationBuilder?.add("key.rmscmod.open_menu", "Open RMSC Menu")
        translationBuilder?.add("category.rmscmod", "RMSC Mod")
    }
}