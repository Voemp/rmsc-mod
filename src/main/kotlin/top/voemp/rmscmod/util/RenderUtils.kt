package top.voemp.rmscmod.util

import net.minecraft.client.render.Camera
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

object RenderUtils {
    fun drawBlockOutline(
        pos: BlockPos,
        r: Float, g: Float, b: Float,
        matrices: MatrixStack,
        camera: Camera,
        provider: VertexConsumerProvider
    ) {
        val box = Box(pos).offset(-camera.pos.x, -camera.pos.y, -camera.pos.z)
        WorldRenderer.drawBox(
            matrices,
            provider.getBuffer(RenderLayer.getLines()),
            box,
            r, g, b, 1f
        )
    }

    fun drawRegionBoxOutline(
        p1: BlockPos,
        p2: BlockPos,
        r: Float, g: Float, b: Float,
        matrices: MatrixStack,
        camera: Camera,
        provider: VertexConsumerProvider
    ) {
        val min = BlockPos(min(p1.x, p2.x), min(p1.y, p2.y), min(p1.z, p2.z))
        val max = BlockPos(max(p1.x, p2.x), max(p1.y, p2.y), max(p1.z, p2.z)).add(1, 1, 1)
        val box = Box(
            Vec3d(min.x.toDouble(), min.y.toDouble(), min.z.toDouble()),
            Vec3d(max.x.toDouble(), max.y.toDouble(), max.z.toDouble())
        ).offset(-camera.pos.x, -camera.pos.y, -camera.pos.z)
        WorldRenderer.drawBox(
            matrices,
            provider.getBuffer(RenderLayer.getLines()),
            box,
            r, g, b, 1f
        )
    }
}