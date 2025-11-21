package com.github.debris.debrisclient.util;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.Objects;
import java.util.function.Predicate;

public class ItemUtil {
    public static boolean compareID(ItemStack itemStack, ItemStack other) {
        return ItemStack.isSameItem(itemStack, other);
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

    public static boolean isFullStack(ItemStack itemStack) {
        return itemStack.getCount() >= itemStack.getMaxStackSize();
    }

    public static boolean canMerge(ItemStack to, ItemStack from) {
        if (isFullStack(to)) return false;
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
