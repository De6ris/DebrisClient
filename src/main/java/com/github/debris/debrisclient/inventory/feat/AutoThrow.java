package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.InventoryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class AutoThrow {
    public static void runAutoThrow() {
        List<Item> list = parseItemList(DCCommonConfig.AutoThrowWhiteList.getStrings());
        if (list.isEmpty()) return;
        for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
            if (isItemInList(slot.getItem(), list)) {
                InventoryUtil.dropStack(slot);
            }
        }
    }

    private static List<Item> parseItemList(List<String> list) {
        return list.stream().map(ResourceLocation::parse).map(BuiltInRegistries.ITEM::getValue).filter(x -> x != Items.AIR).toList();
    }

    private static boolean isItemInList(ItemStack itemStack, List<Item> items) {
        if (itemStack.isEmpty()) return false;
        for (Item item : items) {
            if (itemStack.is(item)) {
                return true;
            }
        }
        return false;
    }
}
