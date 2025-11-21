package com.github.debris.debrisclient.inventory.sort;

import com.github.debris.debrisclient.util.ItemUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ItemStackComparators {
    public static final Comparator<ItemStack> COUNT = Comparator.comparing(ItemStack::getCount);
    public static final Comparator<ItemStack> SHULKER_BOX = ItemStackComparators::compareShulkerBox;
    public static final Comparator<ItemStack> BUNDLE = ItemStackComparators::compareBundle;
    public static final Comparator<ItemStack> ENCHANTMENT = Comparator.comparing(x -> x.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().size());
    public static final Comparator<ItemStack> DAMAGE = Comparator.comparing(ItemStack::getDamageValue);

    public static int compareShulkerBox(ItemStack c1, ItemStack c2) {
        if (ItemUtil.isShulkerBox(c1) && ItemUtil.isShulkerBox(c2)) {
            List<ItemStack> list1 = c1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().toList();
            List<ItemStack> list2 = c2.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().toList();
            int compare = Integer.compare(list1.size(), list2.size());// comparing size
            if (compare != 0) return compare;
            if (list1.isEmpty()) return 0;
            if (isPureItemStackList(list1) && isPureItemStackList(list2)) {
                ItemStack itemStack1 = list1.getFirst();
                ItemStack itemStack2 = list2.getFirst();
                return SortCategory.getItemStackSorter().compare(itemStack1, itemStack2);
            }
        }
        return 0;
    }

    public static int compareBundle(ItemStack c1, ItemStack c2) {
        if (ItemUtil.isBundle(c1) && ItemUtil.isBundle(c2)) {
            BundleContents bundle1 = c1.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            BundleContents bundle2 = c2.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            int compare = bundle1.weight().compareTo(bundle2.weight());// comparing occupancy fraction
            if (compare != 0) return compare;
            return Integer.compare(bundle1.size(), bundle2.size());// comparing item list size
        }
        return 0;
    }

    // Assuming no empty stack and non-empty list
    private static boolean isPureItemStackList(List<ItemStack> list) {
        ItemStack first = list.getFirst();
        Predicate<ItemStack> predicate = ItemUtil.predicateIDMeta(first);
        return list.stream().allMatch(predicate);
    }
}
