package top.voemp.rmscmod.serial

import com.fazecast.jSerialComm.SerialPort
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import top.voemp.rmscmod.RMSCMod.MOD_ID
import top.voemp.rmscmod.RMSCMod.logger
import top.voemp.rmscmod.serial.DataUtils.lineOf
import top.voemp.rmscmod.serial.DataUtils.toConfigData
import java.nio.file.Files
import java.nio.file.Path

object SerialManager {
    private var serialPort: SerialPort? = null
    val serialConfig: SerialConfig = loadConfig()
    val lastSerialConfig: SerialConfig = loadConfig()

    private fun configDir(): Path {
        val configDir = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        Files.createDirectories(configDir)
        return configDir
    }

    fun saveConfig() {
        val configFile = configDir().resolve("serial_config.json")
        Files.writeString(configFile, Gson().toJson(serialConfig))
        lastSerialConfig.portDescriptor = serialConfig.portDescriptor
        lastSerialConfig.baudRate = serialConfig.baudRate
        logger.info("Serial config saved")
    }

    fun loadConfig(): SerialConfig {
        val configFile = configDir().resolve("serial_config.json")
        return if (Files.exists(configFile)) {
            Gson().fromJson(Files.readString(configFile), SerialConfig::class.java)
        } else SerialConfig()
    }

    fun hasConfig(): Boolean {
        return serialConfig.portDescriptor.isNotEmpty() && serialConfig.baudRate > 0
    }

    fun isConnected(): Boolean {
        return serialPort?.isOpen ?: false
    }

    fun isAvailable(): Boolean {
        return (serialPort?.bytesAvailable() ?: 0) > 0
    }

    fun openPort(): Boolean {
        serialPort = SerialPort.getCommPort(serialConfig.portDescriptor)
        serialPort?.baudRate = serialConfig.baudRate
        return serialPort?.openPort() ?: false
    }

    fun closePort() {
        serialPort?.closePort()
        logger.info("Serial port closed")
    }

    fun read(): List<Byte>? {
        val port = serialPort ?: return null
        val buffer = ByteArray(64)
        port.readBytes(buffer, buffer.size, 0)
        val start = buffer.indexOf('&'.code.toByte())
        val end = buffer.indexOf('\n'.code.toByte())
        return if (start < end) buffer.slice(start + 1 until end) else null
    }

    fun write(data: List<Byte>) {
        serialPort?.writeBytes(data.toByteArray(), data.size)
    }

    fun startSerialListener(client: MinecraftClient) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isConnected()) {
                Thread.sleep(100)
                val data = read()
                if (data == null) continue
                logger.info("Received data: $data")
                when (data[0]) {
                    1.toByte() -> DataManager.refreshPage()
                    2.toByte() -> DataManager.nextPage()
                    3.toByte() -> DataManager.prevPage()
                    4.toByte() -> DataManager.nextLine()
                    5.toByte() -> DataManager.prevLine()
                    6.toByte() -> DataManager.moreLine()
                    7.toByte() -> write(lineOf("1").toConfigData())
                }
            }
            logger.info("Serial listener stopped")
        }
    }
}
