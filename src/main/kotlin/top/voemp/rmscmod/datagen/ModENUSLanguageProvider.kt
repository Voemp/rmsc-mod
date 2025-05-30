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
        translationBuilder?.add("menu.rmscmod.editConfigScreen.title", "Edit Config")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.enterName", "Config Name")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.areaPos", "Pos %s")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.world", "World: [%s]")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.switchPos", "Switch %s: [%s]")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.removeSwitch", "Remove")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.clearAreaSelection", "Clear Area")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.clearSwitchSelection", "Clear Switch")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.noSwitch", "No Switch Selected")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.saveConfig", "Save Config")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmSave", "Save Config")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmSave.message", "Are you sure to save the config?")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmCover", "Cover Config")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmCover.message", "The config with the same ID already exists. This will cover the current config. Are you sure?")
        translationBuilder?.add("menu.rmscmod.configListScreen.title", "Config List")
        translationBuilder?.add("menu.rmscmod.editConfig", "Edit Config")
        translationBuilder?.add("menu.rmscmod.deleteConfig", "Delete Config")
        translationBuilder?.add("config.rmscmod.newConfig", "New Config")
        translationBuilder?.add("key.rmscmod.openMenu", "Open RMSC Menu")
        translationBuilder?.add("category.rmscmod", "RMSC Mod")
    }
}