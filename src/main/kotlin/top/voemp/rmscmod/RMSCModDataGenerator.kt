package top.voemp.rmscmod

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import top.voemp.rmscmod.datagen.ModBlockTagProvider
import top.voemp.rmscmod.datagen.ModENUSLanguageProvider

object RMSCModDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()

        pack.addProvider(::ModBlockTagProvider)
        pack.addProvider(::ModENUSLanguageProvider)
    }
}