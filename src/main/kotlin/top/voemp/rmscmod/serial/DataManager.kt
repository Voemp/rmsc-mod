package top.voemp.rmscmod.serial

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.network.ModPayloads
import top.voemp.rmscmod.serial.DataUtils.lineEmpty
import top.voemp.rmscmod.serial.DataUtils.lineOf
import top.voemp.rmscmod.serial.DataUtils.lineUnchanged
import top.voemp.rmscmod.serial.DataUtils.toConfigData

object DataManager {
    private val PLACEHOLDER_CONFIG = listOf(SimplifyConfig("§", 0))

    private var configData: List<SimplifyConfig> = listOf()
    private var inventoryData: List<String> = listOf()

    private var curPage: Int = 0
    private var compareAns: MutableList<Boolean> = mutableListOf()

    private var page1Cache: List<SimplifyConfig> = listOf()
    private var page1Index = 0
    private var page2Cache: List<String> = listOf()
    private var page2Index = 0

    fun init() {
        getConfigData()
        clearData()
    }

    fun getConfigData() {
        configData = ConfigManager.loadAllConfigs().map {
            SimplifyConfig(
                name = it.name,
                status = 0
                    .plus(if (it.switchStatus) 1 else 0)
                    .plus(if (it.switchSet != null) 2 else 0)
                    .plus(if (it.areaSelection != null) 4 else 0)
            )
        }
    }

    fun getInventoryData(name: String) {
        val config = ConfigManager.loadConfig(name)
        if (config?.areaSelection == null) return

        ClientPlayNetworking.send(ModPayloads.AreaSelectionC2SPayload(config.areaSelection))
        ClientPlayNetworking.registerGlobalReceiver(ModPayloads.ItemListS2CPayload.ID) { payload, context ->
            if (context.client().world == null) return@registerGlobalReceiver
            inventoryData = payload.itemList
        }
    }

    fun clearData() {
        page1Cache = listOf()
        page1Index = 0
        page2Cache = listOf()
        page2Index = 0
        curPage = 0
        compareAns = mutableListOf()
    }

    fun refreshPage() {
        when (curPage) {
            1 -> {
                compareAns = compareCache(page1Cache, page1Index, configData)
                println(compareAns)
                page1Cache = configData.drop(page1Index).take(4)
                val message = makeMessage(page1Index, configData).toConfigData()
                SerialManager.write(message)
                println(page1Cache)
                println(message)
            }
        }
    }

    fun nextPage() {
        curPage++
        if (curPage == 2) {
            page2Cache = listOf()
            page2Index = 0
        }
        refreshPage()
    }

    fun prevPage() {
        curPage--
        refreshPage()
    }

    fun moreLine() {
        when (curPage) {
            1 -> {
                SerialManager.write(makeMessage(page1Index, configData).toConfigData())
            }
        }
    }

    fun nextLine() {
        when (curPage) {
            1 -> {
                if (page1Index < configData.size - 4) page1Index++
                page1Cache = page1Cache.drop(1).take(3)
                refreshPage()
            }
        }
    }

    fun prevLine() {
        when (curPage) {
            1 -> {
                if (page1Index > 0) page1Index--
                page1Cache = PLACEHOLDER_CONFIG + page1Cache.drop(0).take(3)
                refreshPage()
            }
        }
    }

    fun makeMessage(index: Int, data: List<Any>): List<Byte> {
        val message = mutableListOf<Byte>()
        while (compareAns.isNotEmpty()) {
            val index = index + 4 - compareAns.size
            if (!compareAns[0]) message += lineUnchanged()
            else if (index >= data.size) message += lineEmpty()
            else {
                val item = data[index]
                message += if (item is SimplifyConfig) lineOf(item.name, item.status)
                else lineOf(item as String)
                compareAns.removeFirst()
                break
            }
            compareAns.removeFirst()
        }
        return message
    }

    fun compareCache(cache: List<Any>, index: Int, data: List<Any>): MutableList<Boolean> {
        val ans = mutableListOf<Boolean>()
        for (i in index until index + 4) {
            if (i >= data.size && i - index >= cache.size) ans.add(false)
            else if (i >= data.size || i - index >= cache.size) ans.add(true)
            else ans.add(cache[i - index] != data[i])
        }
        return ans
    }
}