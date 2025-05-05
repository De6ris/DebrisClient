package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.InventoryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.List;

public class AutoThrow {
    public static void runAutoThrow() {
        List<Item> list = parseItemList(DCCommonConfig.AutoThrowWhiteList.getStrings());
        if (list.isEmpty()) return;
        for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
            if (isItemInList(slot.getStack(), list)) {
                InventoryUtil.dropStack(slot);
            }
        }
    }

    private static List<Item> parseItemList(List<String> list) {
        return list.stream().map(Identifier::of).map(Registries.ITEM::get).filter(x -> x != Items.AIR).toList();
    }

    private static boolean isItemInList(ItemStack itemStack, List<Item> items) {
        if (itemStack.isEmpty()) return false;
        for (Item item : items) {
            if (itemStack.isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
