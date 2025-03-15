package com.github.Debris.DebrisClient.inventory.util;


import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class ItemUtil {
    public static boolean compareID(ItemStack itemStack, ItemStack other) {
        return ItemStack.areItemsEqual(itemStack, other);
    }

    public static boolean compareMeta(ItemStack itemStack, ItemStack other) {
        return Objects.equals(itemStack.getComponents(), other.getComponents());
    }

    public static boolean compareIDMeta(ItemStack itemStack, ItemStack other) {
        return compareID(itemStack, other) && compareMeta(itemStack, other);
    }

    public static Predicate<ItemStack> predicateID(ItemStack template) {
        return x -> compareID(x, template);
    }

    public static Predicate<ItemStack> predicateIDMeta(ItemStack template) {
        return x -> compareIDMeta(x, template);
    }

    public static boolean canMerge(ItemStack to, ItemStack from) {
        if (to.getCount() >= to.getMaxCount()) return false;// full slot can not merge
        return compareIDMeta(to, from);
    }
}
