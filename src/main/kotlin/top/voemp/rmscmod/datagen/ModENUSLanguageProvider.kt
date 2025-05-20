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
        translationBuilder?.add("menu.rmscmod.title", "RMSC Menu")
        translationBuilder?.add("menu.rmscmod.tab.save.title", "Save Config")
        translationBuilder?.add("menu.rmscmod.tab.save.enterName", "Config Name")
        translationBuilder?.add("menu.rmscmod.tab.save.world", "World: %s")
        translationBuilder?.add("menu.rmscmod.tab.save.clearAreaSelection", "Clear Area")
        translationBuilder?.add("menu.rmscmod.tab.save.clearSwitchSelection", "Clear Switch")
        translationBuilder?.add("menu.rmscmod.tab.save.noSwitch", "No Switch Selected")
        translationBuilder?.add("menu.rmscmod.tab.save.removeSwitch", "Remove")
        translationBuilder?.add("menu.rmscmod.tab.save.saveConfig", "Save Config")
        translationBuilder?.add("config.rmscmod.newConfig", "New Config")
        translationBuilder?.add("key.rmscmod.openMenu", "Open RMSC Menu")
        translationBuilder?.add("category.rmscmod", "RMSC Mod")
    }
}