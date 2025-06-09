package top.voemp.rmscmod.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class ModZHCNLanguageProvider : FabricLanguageProvider {
    constructor(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>) : super(
        dataOutput,
        "zh_cn",
        registryLookup
    )

    override fun generateTranslations(
        registryLookup: RegistryWrapper.WrapperLookup?,
        translationBuilder: TranslationBuilder?
    ) {
        translationBuilder?.add("menu.rmscmod.title", "RMSC 菜单")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.title", "编辑配置")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.enterConfigName", "配置名称")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.areaPos", "点 %s")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.world", "维度：[%s]")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.switchPos", "开关 %s：[%s]")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.removeSwitch", "删除")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.clearAreaSelection", "清除选区")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.clearSwitchSelection", "清除开关")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.noSwitch", "未选择开关")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.saveConfig", "保存配置")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.saveConfig.tooltip", "请先选择区域或开关")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.cancelEdit", "取消编辑")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.cancelEdit.tooltip", "当前没有已加载的配置")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmSave", "保存配置")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmSave.message", "确定要保存配置吗？")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmOverride", "覆盖配置")
        translationBuilder?.add("menu.rmscmod.editConfigScreen.confirmOverride.message", "已存在相同名称的配置，这将覆盖当前配置。确定覆盖？")
        translationBuilder?.add("menu.rmscmod.configListScreen.title", "配置列表")
        translationBuilder?.add("menu.rmscmod.configListScreen.areaSelection", "选区大小：%s x %s x %s")
        translationBuilder?.add("menu.rmscmod.configListScreen.noAreaSelection", "没有选区")
        translationBuilder?.add("menu.rmscmod.configListScreen.switchSelection", "开关数量：%s")
        translationBuilder?.add("menu.rmscmod.configListScreen.noSwitchSelection", "没有开关")
        translationBuilder?.add("menu.rmscmod.configListScreen.editConfig", "编辑配置")
        translationBuilder?.add("menu.rmscmod.configListScreen.deleteConfig", "删除配置")
        translationBuilder?.add("menu.rmscmod.serialScreen.title", "连接设备")
        translationBuilder?.add("menu.rmscmod.serialScreen.enterPortDescriptor", "端口")
        translationBuilder?.add("menu.rmscmod.serialScreen.enterBaudRate", "波特率")
        translationBuilder?.add("menu.rmscmod.serialScreen.connect", "连接")
        translationBuilder?.add("menu.rmscmod.serialScreen.disconnect", "断开")
        translationBuilder?.add("config.rmscmod.newConfig", "新的配置")
        translationBuilder?.add("config.rmscmod.newConfig.suffix", "（%s）")
        translationBuilder?.add("key.rmscmod.openMenu", "打开 RMSC 菜单")
        translationBuilder?.add("category.rmscmod", "RMSC 模组")
    }
}