package top.voemp.rmscmod.config

import top.voemp.rmscmod.selection.AreaPosWithWorld
import top.voemp.rmscmod.selection.BlockPosWithWorld

data class ModConfig(
    val name: String,
    val time: Long,
    var switchStatus: Boolean,
    val areaSelection: AreaPosWithWorld?,
    val switchSet: Set<BlockPosWithWorld>?
)