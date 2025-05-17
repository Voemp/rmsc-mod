package top.voemp.rmscmod.render

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.util.math.MatrixStack
import top.voemp.rmscmod.selection.SelectionManager
import top.voemp.rmscmod.util.RenderUtils.drawBlockOutline
import top.voemp.rmscmod.util.RenderUtils.drawRegionBoxOutline

object WorldRender {
    fun renderSelectionPreview(context: WorldRenderContext) {
        if (!SelectionManager.isActive) return

        val matrices = MatrixStack()
        val camera = context.camera()
        val provider = context.consumers() ?: return

        val p1 = SelectionManager.point1
        val p2 = SelectionManager.point2
        val spSet = SelectionManager.switchPosSet

        if (p1 != null && p2 != null) {
            drawRegionBoxOutline(p1, p2, 1f, 1f, 1f, matrices, camera, provider)
        }
        p1?.let {
            drawBlockOutline(it, 1f, 0f, 0f, matrices, camera, provider)
        }
        p2?.let {
            drawBlockOutline(it, 0f, 0.4f, 1f, matrices, camera, provider)
        }

        spSet.forEach {
            drawBlockOutline(it, 1f, 1f, 0f, matrices, camera, provider)
        }
    }

    fun registerWorldRenderEvents() {
        WorldRenderEvents.BEFORE_ENTITIES.register(WorldRenderEvents.BeforeEntities { context ->
            renderSelectionPreview(context)
        })
    }
}