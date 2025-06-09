package top.voemp.rmscmod.serial

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.minecraft.client.MinecraftClient
import top.voemp.rmscmod.RMSCMod.logger

object SerialManager {
    private var serialPort: SerialPort? = null
    private var serialListener: Job? = null
    var portDescriptor: String = "COM4"
    var baudRate: Int = 115200

    fun connect(): Boolean {
        serialPort = SerialPort.getCommPort(portDescriptor)
        serialPort?.baudRate = baudRate
        return serialPort?.openPort() ?: false
    }

    fun read(): String? {
        val port = serialPort ?: return null
        val buffer = ByteArray(1024)
        port.readBytes(buffer, buffer.size, 0)
        val offset = buffer.indexOf(64)
        val numBytes = buffer.indexOf(1.toByte()) + 26
        return if (offset >= 0) String(buffer, offset, 26) else null
    }

    fun write(data: String) {
        serialPort?.writeBytes(data.toByteArray(), data.length)
    }

    fun close() {
        serialPort?.closePort()
    }

    fun startSerialListener(client: MinecraftClient) {
        serialListener = CoroutineScope(Dispatchers.IO).launch {
            while (serialPort?.isOpen == true) {
                Thread.sleep(500)
                val data = read()
                if (data != null) {
                    client.execute {
                        logger.info("Received data: $data")
                    }
                }
            }
            logger.info("Serial port closed")
        }
    }
}
