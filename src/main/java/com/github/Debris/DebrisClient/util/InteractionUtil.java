package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.feat.PlayerRotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class InteractionUtil {
    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(MinecraftClient client, BlockPos blockPos) {
        return client.player.canInteractWithBlockAt(blockPos, 0);
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean withinReach(MinecraftClient client, Entity entity) {
        return client.player.canInteractWithEntity(entity, 0);
    }

    @SuppressWarnings("ConstantConditions")
    public static void attackBlock(MinecraftClient client, BlockPos pos) {
        client.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
    }

    public static ActionResult interactBlock(MinecraftClient client, BlockPos blockPos) {
        return interactBlock(client, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.UP, blockPos, false));
    }

    @SuppressWarnings("ConstantConditions")
    public static ActionResult interactBlock(MinecraftClient client, BlockHitResult hitResult) {
        return client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hitResult);
    }

    @SuppressWarnings("DataFlowIssue")
    public static ActionResult interactEntity(MinecraftClient client, Entity entity) {
        return client.interactionManager.interactEntity(client.player, entity, Hand.MAIN_HAND);
    }

    @SuppressWarnings("DataFlowIssue")
    public static ActionResult interactEntityAtLocation(MinecraftClient client, Entity entity, EntityHitResult hitResult) {
        return client.interactionManager.interactEntityAtLocation(client.player, entity, hitResult, Hand.MAIN_HAND);
    }

    @SuppressWarnings("DataFlowIssue")
    public static ActionResult interactItem(MinecraftClient client, Hand hand) {
        return client.interactionManager.interactItem(client.player, hand);
    }

    @SuppressWarnings("ConstantConditions")
    public static void attackEntity(MinecraftClient client, Entity target) {
        client.interactionManager.attackEntity(client.player, target);
    }

    /**
     * See {@link MinecraftClient#doItemUse()}
     */
    @SuppressWarnings("DataFlowIssue")
    public static void useEntity(MinecraftClient client, Entity entity) {
        for (Hand hand : Hand.values()) {
//            ItemStack itemStack = client.player.getStackInHand(hand);

            ActionResult actionResult = interactEntityAtLocation(client, entity, new EntityHitResult(entity));
            if (!actionResult.isAccepted()) {
                actionResult = interactEntity(client, entity);
            }

            if (actionResult instanceof ActionResult.Success success) {
                if (success.swingSource() == ActionResult.SwingSource.CLIENT) {
                    client.player.swingHand(Hand.MAIN_HAND);
                }

                return;
            }

//            if (!itemStack.isEmpty() && syncLooking(client, entity) && interactItem(client, hand) instanceof ActionResult.Success success3) {
//                if (success3.swingSource() == ActionResult.SwingSource.CLIENT) {
//                    client.player.swingHand(hand);
//                }
//
//                client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
//                return;
//            }
        }
    }

    /**
     * See {@link MinecraftClient#doItemUse()}
     */
    @SuppressWarnings("DataFlowIssue")
    public static void useBlock(MinecraftClient client, BlockPos pos) {
        for (Hand hand : Hand.values()) {
            ItemStack itemStack = client.player.getStackInHand(hand);

            int i = itemStack.getCount();
            ActionResult actionResult2 = interactBlock(client, pos);
            if (actionResult2 instanceof ActionResult.Success success2) {
                if (success2.swingSource() == ActionResult.SwingSource.CLIENT) {
                    client.player.swingHand(hand);
                    if (!itemStack.isEmpty() && (itemStack.getCount() != i || client.player.isInCreativeMode())) {
                        client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                    }
                }

                return;
            }

            if (actionResult2 instanceof ActionResult.Fail) {
                return;
            }

//            if (!itemStack.isEmpty() && syncLooking(client, pos) && interactItem(client, hand) instanceof ActionResult.Success success3) {
//                if (success3.swingSource() == ActionResult.SwingSource.CLIENT) {
//                    client.player.swingHand(hand);
//                }
//
//                client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
//                return;
//            }
        }
    }

    private static boolean syncLooking(MinecraftClient client, Entity entity) {
        PlayerRotation.lookAtEntity(client.player, entity);
        return true;
    }

    private static boolean syncLooking(MinecraftClient client, BlockPos pos) {
        PlayerRotation.lookAtBlockPos(client.player, pos);
        return true;
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
}
