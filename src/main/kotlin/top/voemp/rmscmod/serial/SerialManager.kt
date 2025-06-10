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
import java.nio.file.Files
import java.nio.file.Path

object SerialManager {
    private var serialPort: SerialPort? = null
    private var serialScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    var serialConfig: SerialConfig = SerialConfig()
    var isConnected = false

    fun init() {
        serialScope.launch {
            serialConfig = loadConfig()
            openPort()
        }
    }

    private fun configDir(): Path {
        val configDir = FabricLoader.getInstance().configDir.resolve(MOD_ID)
        Files.createDirectories(configDir)
        return configDir
    }

    fun saveConfig() {
        val configFile = configDir().resolve("serial_config.json")
        Files.writeString(configFile, Gson().toJson(serialConfig))
    }

    private fun loadConfig(): SerialConfig {
        val configFile = configDir().resolve("serial_config.json")
        return if (Files.exists(configFile)) {
            Gson().fromJson(Files.readString(configFile), SerialConfig::class.java)
        } else SerialConfig()
    }

    fun hasConfig(): Boolean {
        return serialConfig.portDescriptor.isNotEmpty() && serialConfig.baudRate > 0
    }

    fun portIsOpen(): Boolean {
        return serialPort?.isOpen ?: false
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
        val buffer = ByteArray(1024)
        port.readBytes(buffer, buffer.size, 0)
        val start = buffer.indexOf(38.toByte())
        val end = buffer.indexOf(10.toByte())
        return if (start >= 0 && end >= 0) buffer.slice(start + 1 until end) else null
    }

    fun write(data: List<Byte>) {
        serialPort?.writeBytes(data.toByteArray(), data.size)
    }

    fun startSerialListener(client: MinecraftClient) {
        serialScope.launch {
            while (portIsOpen() && isConnected) {
                Thread.sleep(100)
                val data = read()
                if (data == null || data.isEmpty()) continue
                client.execute {
                    logger.info("Received data: $data")
                    when (data[0]) {
                        1.toByte(), 2.toByte(), 3.toByte() -> write(lineOf("黏液球 65"))
                        4.toByte(), 5.toByte() -> write(lineOf("黏液球 64"))
                        6.toByte() -> write(lineOf("凋零骷髅头颅 162.3K"))
                    }
                }
            }
            logger.info("Serial listener stopped")
        }
    }
}
