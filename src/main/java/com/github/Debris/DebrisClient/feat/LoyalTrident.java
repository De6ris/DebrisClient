package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.slf4j.Logger;

public class LoyalTrident {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ThrownContext CONTEXT = ThrownContext.EMPTY;
    private static boolean THROWN = false;

    @Environment(EnvType.CLIENT)
    public static void onTridentThrown(PlayerEntity playerEntity, ItemStack stack) {
        if (!DCCommonConfig.LoyalerTrident.getBooleanValue()) return;
        PlayerInventory inventory = playerEntity.getInventory();
        if (ItemStack.areEqual(inventory.offHand.getFirst(), stack)) {
            CONTEXT = new ThrownContext(stack, 40);
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (ItemStack.areEqual(inventory.main.get(i), stack)) {
                CONTEXT = new ThrownContext(stack, i);
                return;
            }
        }
        LOGGER.warn("fail to analyze thrown context");
        CONTEXT = ThrownContext.EMPTY;
    }

    public static void onClientTick(MinecraftClient minecraftClient) {
        if (!DCCommonConfig.LoyalerTrident.getBooleanValue()) return;
        if (CONTEXT.isEmpty()) return;
        ClientPlayerEntity player = minecraftClient.player;
        if (player == null) return;
        checkThrown(player);
        if (THROWN) {// should not search before thrown
            ItemEnchantmentsComponent enchantments = CONTEXT.stack.getEnchantments();
            for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
                ItemStack stack = slot.getStack();
                if (stack.isOf(Items.TRIDENT) && stack.getEnchantments().equals(enchantments)) {
                    onTridentFound(slot);
                    return;
                }
            }
        }

    }

    private static void checkThrown(PlayerEntity playerEntity) {
        PlayerInventory inventory = playerEntity.getInventory();
        int index = CONTEXT.index;
        if (index == 40) {
            if (inventory.offHand.getFirst().isEmpty()) {
                THROWN = true;
            }
        } else {
            if (inventory.main.get(index).isEmpty()) {
                THROWN = true;
            }
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
