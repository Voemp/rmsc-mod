package top.voemp.rmscmod.serial

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import top.voemp.rmscmod.config.ConfigManager
import top.voemp.rmscmod.network.ModPayloads
import top.voemp.rmscmod.serial.DataUtils.lineEmpty
import top.voemp.rmscmod.serial.DataUtils.lineOf
import top.voemp.rmscmod.serial.DataUtils.lineUnchanged
import top.voemp.rmscmod.serial.DataUtils.toConfigData
import top.voemp.rmscmod.serial.DataUtils.toInventoryData

object DataManager {
    private val PLACEHOLDER_CONFIG = listOf(SimplifyConfig("§", 0))
    private val PLACEHOLDER_ITEM = listOf("§")
    private val placeholderLine: List<List<Any>> = listOf(PLACEHOLDER_CONFIG, PLACEHOLDER_ITEM)

    private var page: Int = -1
    private var compareAns: MutableList<Boolean> = mutableListOf()
    private var forceRefresh = false
    private var changeResult = false

    private val pageData: MutableList<List<Any>> = mutableListOf(listOf(), listOf())
    private val pageCache: MutableList<List<Any>> = mutableListOf(listOf(), listOf())
    private val pageIndex: MutableList<Int> = mutableListOf(0, 0)

    fun init() {
        getConfigData()
        clearData()
    }

    fun getConfigData() {
        pageData[0] = ConfigManager.loadAllConfigs().map {
            SimplifyConfig(
                name = it.name,
                status = 0
                    .plus(if (it.switchStatus) 1 else 0)
                    .plus(if (it.switchSet != null) 2 else 0)
                    .plus(if (it.areaSelection != null) 4 else 0)
            )
        }
    }

    private fun getInventoryData(name: String) {
        val config = ConfigManager.loadConfig(name)
        config?.areaSelection?.let {
            ClientPlayNetworking.send(ModPayloads.AreaSelectionC2SPayload(it))
            Thread.sleep(50)
        }
    }

    private fun changeSwitchStatus(name: String) {
        val config = ConfigManager.loadConfig(name)
        config?.switchSet?.let {
            ClientPlayNetworking.send(ModPayloads.SwitchListC2SPayload(it.toList()))
            var timer = 100
            while (timer > 0 && !changeResult) {
                Thread.sleep(50)
                timer--
            }
            if (!changeResult) return
            config.switchStatus = !config.switchStatus
            ConfigManager.saveConfig(config)
        }
    }

    fun registerDataManagerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ModPayloads.ItemListS2CPayload.ID) { payload, context ->
            if (context.client().world == null) return@registerGlobalReceiver
            pageData[1] = payload.itemList
        }
        ClientPlayNetworking.registerGlobalReceiver(ModPayloads.SwitchChangeResultS2CPayload.ID) { payload, context ->
            if (context.client().world == null || !payload.changeResult) return@registerGlobalReceiver
            changeResult = true
        }
    }

     private fun clearData() {
        page = -1
        compareAns = mutableListOf()
        pageCache[0] = listOf()
        pageCache[1] = listOf()
        pageIndex[0] = 0
        pageIndex[1] = 0
    }

    fun refreshPage() {
        if (forceRefresh) {
            compareAns = mutableListOf(true, true, true, true)
            forceRefresh = false
        } else compareAns = compareCache(pageCache[page], pageIndex[page], pageData[page])
        pageCache[page] = pageData[page].drop(pageIndex[page]).take(4)
        SerialManager.write(makeMessage(pageIndex[page], pageData[page]).toCurrentData())
    }

    fun toPage0() {
        page = 0
        forceRefresh = true
        refreshPage()
    }

    fun toPage1(lineIndex: Int) {
        page = 1
        forceRefresh = true
        pageData[1] = listOf()
        pageCache[1] = listOf()
        pageIndex[1] = 0
        getInventoryData((pageCache[0][lineIndex] as SimplifyConfig).name)
        refreshPage()
    }

    fun moreLine() {
        SerialManager.write(makeMessage(pageIndex[page], pageData[page]).toCurrentData())
    }

    fun nextLine() {
        if (pageIndex[page] < pageData[page].size - 4) pageIndex[page]++
        pageCache[page] = pageCache[page].drop(1).take(3)
        refreshPage()
    }

    fun prevLine() {
        if (pageIndex[page] > 0) pageIndex[page]--
        pageCache[page] = placeholderLine[page] + pageCache[page].drop(0).take(3)
        refreshPage()
    }

    fun toggleSwitch(lineIndex: Int) {
        val curConfig = pageCache[0][lineIndex] as SimplifyConfig
        changeSwitchStatus(curConfig.name)
        var newStatus = curConfig.status
        if (changeResult) {
            if (curConfig.status % 2 == 0) newStatus++
            else newStatus--
            curConfig.status = newStatus
            changeResult = false
        }
        SerialManager.write(listOf(newStatus.toByte()).toCurrentData())
    }

    private fun makeMessage(index: Int, data: List<Any>): List<Byte> {
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

    private fun compareCache(cache: List<Any>, index: Int, data: List<Any>): MutableList<Boolean> {
        val ans = mutableListOf<Boolean>()
        for (i in index until index + 4) {
            if (i >= data.size && i - index >= cache.size) ans.add(false)
            else if (i >= data.size || i - index >= cache.size) ans.add(true)
            else ans.add(cache[i - index] != data[i])
        }
        return ans
    }

    private fun List<Byte>.toCurrentData(): List<Byte> {
        return when (page) {
            0 -> this.toConfigData()
            1 -> this.toInventoryData()
            else -> this
        }
    }
}