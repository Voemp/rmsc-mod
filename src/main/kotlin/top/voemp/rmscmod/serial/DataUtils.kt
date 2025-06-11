package top.voemp.rmscmod.serial

object DataUtils {
    /**
     * 将字符串转成行数据
     * @param str 待转换的字符串
     * @return 转换后的行数据
     */
    fun lineOf(str: String): List<Byte> = listOf(32.toByte()) + FontBitmap.of(str) + listOf(64.toByte())

    /**
     * 将字符串转成行数据
     * @param str 待转换的行数据
     * @param status 行数据状态
     * @return 转换后的行数据
     */
    fun lineOf(str: String, status: Int = 0): List<Byte> = listOf(32.toByte()) + FontBitmap.of(str) + listOf((64 + status).toByte())

    /**
     * 获取一个空行数据
     * @return 空行数据
     */
    fun lineEmpty(): List<Byte> = listOf(96.toByte())

    /**
     * 获取一个未改变行数据
     * @return 未改变行数据
     */
    fun lineUnchanged(): List<Byte> = listOf(160.toByte())

    /**
     * 将行数据转成配置行数据
     * @return 转换后的配置行数据
     */
    fun List<Byte>.toConfigData(): List<Byte> = listOf('@'.code.toByte()) + this

    /**
     * 将行数据转成容器行数据
     * @return 转换后的容器行数据
     */
    fun List<Byte>.toInventoryData(): List<Byte> = listOf('#'.code.toByte()) + this
}