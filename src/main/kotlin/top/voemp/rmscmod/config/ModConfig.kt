package top.voemp.rmscmod.config

import top.voemp.rmscmod.selection.AreaPosWithWorld
import top.voemp.rmscmod.selection.BlockPosWithWorld

data class ModConfig(
    val id: String,
    val name: String,
    val time: String?,
    val switchStatus: Boolean,
    val areaSelection: AreaPosWithWorld?,
    val switchSet: Set<BlockPosWithWorld>?
)