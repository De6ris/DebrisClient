package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.UtilCaller;
import fi.dy.masa.malilib.util.GuiUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.input.Input;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.Objects;

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

    public static void runOrientedTrading(MinecraftClient client) {
        if (!DCCommonConfig.OrientedAutoTrading.getBooleanValue()) return;
        if (!FabricLoader.getInstance().isModLoaded("itemscroller")) return;
        Screen currentScreen = GuiUtils.getCurrentScreen();
        List<String> targets = DCCommonConfig.TradingTargets.getStrings();
        if (currentScreen instanceof MerchantScreen merchantScreen) {
            MerchantScreenHandler merchantContainer = merchantScreen.getScreenHandler();
            TradeOfferList recipes = merchantContainer.getRecipes();
            for (int i = 0; i < recipes.size(); i++) {
                ItemStack sellItem = recipes.get(i).getSellItem();
                if (isItemInList(sellItem, targets)) {
                    UtilCaller.tryHardTrading(i);
                }
            }

            Objects.requireNonNull(client.player).closeHandledScreen();
        }
    }

    public static void runAutoThrow() {
        List<String> identifiers = DCCommonConfig.AutoThrowWhiteList.getStrings();
        for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
            if (isItemInList(slot.getStack(), identifiers)) {
                InventoryUtil.drop(slot, true);
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

}
