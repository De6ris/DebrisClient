package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;

public class LoyalTrident {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ThrownContext CONTEXT = ThrownContext.EMPTY;
    private static boolean THROWN = false;

    @Environment(EnvType.CLIENT)
    public static void onTridentThrown(Player playerEntity, ItemStack stack) {
        if (!DCCommonConfig.LoyalerTrident.getBooleanValue()) return;
        Inventory inventory = playerEntity.getInventory();
        if (ItemStack.matches(inventory.getItem(40), stack)) {
            CONTEXT = new ThrownContext(stack, 40);
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (ItemStack.matches(inventory.getItem(i), stack)) {
                CONTEXT = new ThrownContext(stack, i);
                return;
            }
        }
        LOGGER.warn("fail to analyze thrown context");
        CONTEXT = ThrownContext.EMPTY;
    }

    public static void onClientTick(Minecraft minecraftClient) {
        if (!DCCommonConfig.LoyalerTrident.getBooleanValue()) return;
        if (CONTEXT.isEmpty()) return;
        LocalPlayer player = minecraftClient.player;
        if (player == null) return;
        checkThrown(player);
        if (THROWN) {// should not search before thrown
            ItemEnchantments enchantments = CONTEXT.stack.getEnchantments();
            for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
                ItemStack stack = slot.getItem();
                if (stack.is(Items.TRIDENT) && stack.getEnchantments().equals(enchantments)) {
                    onTridentFound(slot);
                    return;
                }
            }
        }

    }

    private static void checkThrown(Player playerEntity) {
        Inventory inventory = playerEntity.getInventory();
        if (inventory.getItem(CONTEXT.index).isEmpty()) {
            THROWN = true;
        }
    }

    /*
     * slot id:
     * 0 craft result
     * 1-4 craft input
     * 5-8 armor 5head, 8feet
     * 9-35 storage
     * 36-44 hotBar
     * 45 offHand
     *
     * slot index:
     * 0-8 hotBar
     * 9-35 storage
     * 36-39 armor 39head, 36feet
     * 40 offHand
     *  */
    private static void onTridentFound(Slot slot) {
        InventoryUtil.swapHotBar(slot, CONTEXT.index);
        CONTEXT = ThrownContext.EMPTY;
        THROWN = false;
    }

    public record ThrownContext(ItemStack stack, int index) {
        private static final ThrownContext EMPTY = new ThrownContext(ItemStack.EMPTY, 0);

        // -1 as off-hand
        private boolean isEmpty() {
            return this == EMPTY;
        }
    }
}
