package top.voemp.rmscmod.serial

object DataUtils {
    fun lineOf(str: String): List<Byte> {
        val bytes = FontBitmap.of(str)
        return listOf(32.toByte()) + bytes + listOf(64.toByte())
    }

    fun List<Byte>.toConfigData(): List<Byte> {
        return listOf('@'.code.toByte()) + this
    }

    fun List<Byte>.toInventoryData(): List<Byte> {
        return listOf('#'.code.toByte()) + this
    }
}