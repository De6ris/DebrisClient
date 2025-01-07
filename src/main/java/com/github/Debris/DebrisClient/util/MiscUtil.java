package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.UtilCaller;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.util.GuiUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.function.Predicate;

public class MiscUtil {
    public static boolean isAutoMoving() {
        return DCCommonConfig.AUTO_WALK.getBooleanValue() ||
                DCCommonConfig.AUTO_LEFT.getBooleanValue() ||
                DCCommonConfig.AUTO_BACK.getBooleanValue() ||
                DCCommonConfig.AUTO_RIGHT.getBooleanValue();
    }

    public static void handleMovement(Input input) {
        PlayerInput oldInput = input.playerInput;
        input.playerInput = new PlayerInput(
                oldInput.forward() || DCCommonConfig.AUTO_WALK.getBooleanValue(),
                oldInput.backward() || DCCommonConfig.AUTO_BACK.getBooleanValue(),
                oldInput.left() || DCCommonConfig.AUTO_LEFT.getBooleanValue(),
                oldInput.right() || DCCommonConfig.AUTO_RIGHT.getBooleanValue(),
                oldInput.jump(),
                oldInput.sneak(),
                oldInput.sprint()
        );
        if (DCCommonConfig.AUTO_WALK.getBooleanValue()) {
            input.movementForward = 1.0F;
        }
        if (DCCommonConfig.AUTO_LEFT.getBooleanValue()) {
            input.movementSideways = 1.0F;
        }
        if (DCCommonConfig.AUTO_RIGHT.getBooleanValue()) {
            input.movementSideways = -1.0F;
        }
        if (DCCommonConfig.AUTO_BACK.getBooleanValue()) {
            input.movementForward = -1.0F;
        }
    }

    public static void clearMovement(Input input) {
        input.playerInput = PlayerInput.DEFAULT;
        input.movementForward = 0.0F;
        input.movementSideways = 0.0F;
    }

    public static void onTradeInfoUpdate(MinecraftClient client) {
        if (DCCommonConfig.OrientedAutoTrading.getBooleanValue()) runOrientedTrading(client);
    }

    public static void runOrientedTrading(MinecraftClient client) {
        if (!FabricLoader.getInstance().isModLoaded("itemscroller")) return;
        Screen currentScreen = client.currentScreen;
        if (currentScreen instanceof MerchantScreen merchantScreen) {
            MerchantScreenHandler merchantContainer = merchantScreen.getScreenHandler();
            TradeOfferList recipes = merchantContainer.getRecipes();
            List<String> targets = DCCommonConfig.TradingTargets.getStrings();
            for (int i = 0; i < recipes.size(); i++) {
                ItemStack sellItem = recipes.get(i).getSellItem();
                if (isItemInList(sellItem, targets)) {
                    UtilCaller.tryHardTrading(i);
                }
            }
            merchantScreen.close();
        }
    }

    public static void runAutoThrow() {
        List<String> identifiers = DCCommonConfig.AutoThrowWhiteList.getStrings();
        for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
            if (isItemInList(slot.getStack(), identifiers)) {
                InventoryUtil.dropStack(slot);
            }
        }
    }

    private static boolean isItemInList(ItemStack itemStack, List<String> identifiers) {
        for (String string : identifiers) {
            Identifier identifier = Identifier.of(string);
            Item item = Registries.ITEM.get(identifier);
            if (itemStack.isOf(item)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean alignWithEnderEye(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;
        ClientPlayerEntity player = client.player;
        Box expand = player.getBoundingBox().expand(32);
        List<Entity> list = Lists.newArrayList();
        client.world.collectEntitiesByType(TypeFilter.instanceOf(EyeOfEnderEntity.class), expand, entity -> true, list, 1);
        if (list.isEmpty()) return false;
        lookAtDirection(player, list.getFirst().getEyePos().subtract(player.getEyePos()));
        return true;
    }

    private static void lookAtDirection(ClientPlayerEntity player, Vec3d directionVec) {
        double dx = directionVec.x;
        double dy = directionVec.y;
        double dz = directionVec.z;
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), yaw, pitch);
    }

    public static void runAutoExtinguisher(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;
        Predicate<BlockState> fireTest = state -> state.isOf(Blocks.FIRE);
        InteractionUtil.digNear(client, fireTest);
    }

}
