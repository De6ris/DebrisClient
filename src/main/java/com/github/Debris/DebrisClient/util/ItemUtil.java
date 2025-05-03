package com.github.Debris.DebrisClient.util;


import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BundleItem;
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

    public static boolean isShulkerBox(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock;
    }

    public static boolean isBundle(ItemStack itemStack) {
        return itemStack.getItem() instanceof BundleItem;
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean isContainer(ItemStack itemStack) {
        if (isShulkerBox(itemStack)) return true;
        if (isBundle(itemStack)) return true;
        return false;
    }
}
