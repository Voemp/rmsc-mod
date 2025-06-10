package top.voemp.rmscmod.serial

object DataUtils {
    fun lineOf(str: String): List<Byte> {
        val bytes = FontBitmap.of(str)
        return listOf(64.toByte(), 32.toByte()) + bytes + listOf(64.toByte())
    }
}