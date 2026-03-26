package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.feat.PlayerRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class InteractionUtil {
    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(Minecraft client, BlockPos blockPos) {
        return client.player.isWithinBlockInteractionRange(blockPos, 0);
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(Minecraft client, Entity entity) {
        return client.player.isWithinEntityInteractionRange(entity, 0);
    }

    @SuppressWarnings("ConstantConditions")
    public static void attackBlock(Minecraft client, BlockPos pos) {
        client.gameMode.continueDestroyBlock(pos, Direction.UP);
    }

    public static InteractionResult interactBlock(Minecraft client, BlockPos blockPos) {
        return interactBlock(client, new BlockHitResult(Vec3.atCenterOf(blockPos), Direction.UP, blockPos, false));
    }

    @SuppressWarnings("ConstantConditions")
    public static InteractionResult interactBlock(Minecraft client, BlockHitResult hitResult) {
        return client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hitResult);
    }

    @SuppressWarnings("DataFlowIssue")
    public static InteractionResult interactEntity(Minecraft client, Entity entity, EntityHitResult hitResult, InteractionHand hand) {
        return client.gameMode.interact(client.player, entity, hitResult, hand);
    }

    @SuppressWarnings("DataFlowIssue")
    public static InteractionResult interactItem(Minecraft client, InteractionHand hand) {
        return client.gameMode.useItem(client.player, hand);
    }

    @SuppressWarnings("ConstantConditions")
    public static void attackEntity(Minecraft client, Entity target) {
        client.gameMode.attack(client.player, target);
    }

    /**
     * See {@link Minecraft#startUseItem()}
     */
    @SuppressWarnings("DataFlowIssue")
    public static void useEntity(Minecraft client, Entity entity) {
        for (InteractionHand hand : InteractionHand.values()) {
            InteractionResult actionResult = interactEntity(client, entity, new EntityHitResult(entity), hand);

            if (actionResult instanceof InteractionResult.Success success) {
                if (success.swingSource() == InteractionResult.SwingSource.CLIENT) {
                    client.player.swing(InteractionHand.MAIN_HAND);
                }

                return;
            }
        }
    }

    /**
     * See {@link Minecraft#startUseItem()}
     */
    @SuppressWarnings("DataFlowIssue")
    public static void useBlock(Minecraft client, BlockPos pos) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = client.player.getItemInHand(hand);

            int i = itemStack.getCount();
            InteractionResult actionResult2 = interactBlock(client, pos);
            if (actionResult2 instanceof InteractionResult.Success success2) {
                if (success2.swingSource() == InteractionResult.SwingSource.CLIENT) {
                    client.player.swing(hand);
                    if (!itemStack.isEmpty() && (itemStack.getCount() != i || client.player.hasInfiniteMaterials())) {
                        client.gameRenderer.itemInHandRenderer.itemUsed(hand);
                    }
                }

                return;
            }

            if (actionResult2 instanceof InteractionResult.Fail) {
                return;
            }
        }
    }

    private static boolean syncLooking(Minecraft client, Entity entity) {
        PlayerRotation.lookAtEntity(client.player, entity);
        return true;
    }

    private static boolean syncLooking(Minecraft client, BlockPos pos) {
        PlayerRotation.lookAtBlockPos(client.player, pos);
        return true;
    }

    /**
     * From 0 to 8 inclusively.
     */
    @SuppressWarnings("ConstantConditions")
    public static int getCurrentHotBar(Minecraft client) {
        return client.player.getInventory().getSelectedSlot();
    }

    @SuppressWarnings("ConstantConditions")
    public static void spectatorTeleport(Minecraft client, UUID playerUUID) {
        client.getConnection().send(new ServerboundTeleportToEntityPacket(playerUUID));
    }
}
