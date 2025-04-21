package com.github.Debris.DebrisClient.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InteractionUtil {
    @Nullable
    private static BlockPos DIG_PROGRESS_POS = null;

    @SuppressWarnings("ConstantConditions")
    public static void digNear(MinecraftClient client, Predicate<BlockState> predicate) {
        BlockPos playerPos = client.player.getBlockPos();
        if (DIG_PROGRESS_POS != null) {// has history
            if (withinReach(client, DIG_PROGRESS_POS) && digAreaAndSwingHand(client, Stream.of(DIG_PROGRESS_POS), predicate).isPresent()) {// keep digging
                return;
            } else {// skip or finish
                DIG_PROGRESS_POS = null;
            }
        }
        digAreaAndSwingHand(client, BlockPos.streamOutwards(playerPos, 4, 4, 4), predicate).ifPresent(pos -> DIG_PROGRESS_POS = pos);
    }

    // return a progressing pos
    @SuppressWarnings("ConstantConditions")
    private static Optional<BlockPos> digAreaAndSwingHand(MinecraftClient client, Stream<BlockPos> source, Predicate<BlockState> predicate) {
        AtomicBoolean swingHand = new AtomicBoolean(false);
        Optional<BlockPos> optional = source.filter(pos -> {
                    DigResult digResult = digSingle(client, pos, predicate);
                    if (!digResult.skipped()) swingHand.set(true);
                    return digResult.inProgress();
                })
                .findFirst();
        if (swingHand.get()) client.player.swingHand(Hand.MAIN_HAND);
        return optional;
    }


    @SuppressWarnings("ConstantConditions")
    private static DigResult digSingle(MinecraftClient client, BlockPos pos, Predicate<BlockState> predicate) {
        ClientWorld world = client.world;
        BlockState currentState = world.getBlockState(pos);
        Block block = currentState.getBlock();
        if (!predicate.test(currentState)) return DigResult.SKIP;
        if (shouldSkipDigging(client, world, pos, currentState)) return DigResult.SKIP;
        attackBlock(client, pos);
        return world.getBlockState(pos).isOf(block) ? DigResult.PROGRESS : DigResult.FINISH;
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(MinecraftClient client, BlockPos blockPos) {
        return client.player.canInteractWithBlockAt(blockPos, 0);
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(MinecraftClient client, Entity entity) {
        return client.player.canInteractWithEntity(entity, 0);
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean shouldSkipDigging(MinecraftClient client, ClientWorld world, BlockPos pos, BlockState state) {
        if (state.isAir()) return true;
        Block block = state.getBlock();
        if (block.getHardness() == -1.0F) return true;
        if (block instanceof FluidBlock) return true;
        if (client.player.isBlockBreakingRestricted(world, pos, client.interactionManager.getCurrentGameMode()))
            return true;
        return false;
    }


    public enum DigResult {
        SKIP,
        PROGRESS,
        FINISH,
        ;

        public boolean skipped() {
            return this == SKIP;
        }

        public boolean inProgress() {
            return this == PROGRESS;
        }

        public boolean finished() {
            return this == FINISH;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void attackBlock(MinecraftClient client, BlockPos pos) {
        client.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
    }

    public static void interactBlock(MinecraftClient client, BlockPos blockPos) {
        interactBlock(client, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.UP, blockPos, false));
    }

    @SuppressWarnings("ConstantConditions")
    public static void interactBlock(MinecraftClient client, BlockHitResult hitResult) {
        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hitResult);
    }

    /**
     * See {@link MinecraftClient#doItemUse()}
     */
    @SuppressWarnings("DataFlowIssue")
    public static void useEntity(MinecraftClient client, Entity entity) {
        ActionResult actionResult = interactEntityAtLocation(client, entity, new EntityHitResult(entity));
        if (!actionResult.isAccepted()) {
            actionResult = interactEntity(client, entity);
        }
        if (actionResult instanceof ActionResult.Success success) {
            if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                client.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static ActionResult interactEntity(MinecraftClient client, Entity entity) {
        return client.interactionManager.interactEntity(client.player, entity, Hand.MAIN_HAND);
    }

    @SuppressWarnings("DataFlowIssue")
    public static ActionResult interactEntityAtLocation(MinecraftClient client, Entity entity, EntityHitResult hitResult) {
        return client.interactionManager.interactEntityAtLocation(client.player, entity, hitResult, Hand.MAIN_HAND);
    }

    /**
     * From 0 to 8 inclusively.
     */
    @SuppressWarnings("ConstantConditions")
    public static int getCurrentHotBar(MinecraftClient client) {
        return client.player.getInventory().getSelectedSlot();
    }

    @SuppressWarnings("ConstantConditions")
    public static void spectatorTeleport(MinecraftClient client, UUID playerUUID) {
        client.getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(playerUUID));
    }

    @SuppressWarnings("ConstantConditions")
    public static void attackEntity(MinecraftClient client, Entity target) {
        client.interactionManager.attackEntity(client.player, target);
    }
}
