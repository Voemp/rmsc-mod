package top.voemp.rmscmod.util

import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import top.voemp.rmscmod.selection.BlockPosWithWorld

object InteractionUtils {
    fun simulateRightClick(
        world: ServerWorld,
        pos: BlockPos,
        facing: Direction = Direction.UP
    ): ActionResult {
        val fakePlayer = FakePlayer.get(world)
        return fakePlayer.interactionManager.interactBlock(
            fakePlayer, world, fakePlayer.mainHandStack, Hand.MAIN_HAND,
            BlockHitResult(Vec3d.ofCenter(pos), facing, pos, false)
        )
    }

    fun changeSwitch(server: MinecraftServer, switch: BlockPosWithWorld): ActionResult {
        val world = server.getWorld(switch.world) ?: return ActionResult.FAIL
        val pos = switch.pos
        return simulateRightClick(world, pos)
    }
}