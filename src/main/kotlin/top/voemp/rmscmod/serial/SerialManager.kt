package top.voemp.rmscmod.serial

import com.fazecast.jSerialComm.SerialPort
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import kotlin.concurrent.thread

object SerialManager {
    private var serialPort: SerialPort? = null

    fun connect(portDescriptor: String, baudRate: Int = 9600): Boolean {
        serialPort = SerialPort.getCommPort(portDescriptor)
        serialPort?.baudRate = baudRate
        return serialPort?.openPort() ?: false
    }

    fun read(): String? {
        val port = serialPort ?: return null
        val buffer = ByteArray(1024)
        val numBytes = port.readBytes(buffer, 0, buffer.size)
        return if (numBytes > 0) String(buffer, 0, numBytes) else null
    }

    fun write(data: String) {
        serialPort?.writeBytes(data.toByteArray(), data.length)
    }

    fun close() {
        serialPort?.closePort()
    }

    fun startSerialListener(client: MinecraftClient) {
        thread(start = true, isDaemon = true) {
            while (true) {
                val msg = read()
                if (msg != null) {
                    // 主线程内调用游戏逻辑
                    client.execute {
                        // 例如：打印消息到聊天窗口
                        client.player?.sendMessage(Text.literal("Arduino: $msg"))
                    }
                }
            }
        }
    }
}
