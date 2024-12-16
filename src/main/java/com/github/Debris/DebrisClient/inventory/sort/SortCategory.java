package com.github.Debris.DebrisClient.inventory.sort;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.PinYinSupport;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Collection;
import java.util.Comparator;

public enum SortCategory {
    TRANSLATION_KEY(Comparator.comparing(Registries.ITEM::getId)),
    CREATIVE_INVENTORY(SortCategory::compareByCreativeInventory),
    TRANSLATION_RESULT(Comparator.comparing(x -> StringUtils.translate(x.getTranslationKey()))),
    PINYIN(SortCategory::compareByPinyin);

    private final Comparator<Item> order;// this assumes they are distinct

    SortCategory(Comparator<Item> order) {
        this.order = order;
    }

    public static SortCategory getCategory() {
        int category = DCCommonConfig.ItemSortingOrder.getIntegerValue();
        return values()[category - 1];// in config, it starts with 1
    }

    /*
     * When using, if result > 0, I will swap.
     * Thus, if you want a comes before b, you should let a be smaller than b in the comparator.
     * */
    public static Comparator<ItemStack> getItemStackSorter() {
        SortCategory category = getCategory();
        setup(category);
        Comparator<Item> itemOrderByConfig = category.order;
        Comparator<ItemStack> itemTypeComparator = (c1, c2) -> {
            if (ItemStack.areItemsEqual(c1, c2)) {
                return 0;
            }
            return itemOrderByConfig.compare(c1.getItem(), c2.getItem());
        };

        return itemTypeComparator
                .thenComparing(ItemStackComparators.COUNT.reversed())// large stacks come first
                .thenComparing(ItemStackComparators.SHULKER_BOX)
                .thenComparing(ItemStackComparators.ENCHANTMENT.reversed())// more enchantments come first
                .thenComparing(ItemStackComparators.DAMAGE)// here damage is lost durability, so lossless items come first
                .thenComparing(x -> x.getName().getString())
                ;
    }

    private static void setup(SortCategory category) {
        switch (category) {
            case CREATIVE_INVENTORY -> {
                if (displayContext == null) {
                    displayContext = buildDisplayContext(MinecraftClient.getInstance());
                }// this make the collection non-empty
            }
            case PINYIN -> {

                if (!loadedPinyinData && PinYinSupport.dataExists()) {
                    loadedPinyinData = PinYinSupport.tryLoad();
                }
            }
        }
    }

    private static int compareByCreativeInventory(Item c1, Item c2) {
        ItemGroup searchGroup = ItemGroups.getSearchGroup();
        Collection<ItemStack> displayStacks = searchGroup.getDisplayStacks();
        for (ItemStack displayStack : displayStacks) {
            if (displayStack.isOf(c1)) {
                return -1;
            }
            if (displayStack.isOf(c2)) {
                return 1;
            }
        }
        return TRANSLATION_KEY.order.compare(c1, c2);
    }

    private static int compareByPinyin(Item c1, Item c2) {
        if (loadedPinyinData) {
            String translate1 = StringUtils.translate(c1.getTranslationKey());
            String translate2 = StringUtils.translate(c2.getTranslationKey());
            return PinYinSupport.compareString(translate1, translate2, () -> TRANSLATION_KEY.order.compare(c1, c2));
        }

        return TRANSLATION_KEY.order.compare(c1, c2);
    }

    private static ItemGroup.DisplayContext buildDisplayContext(MinecraftClient mc) {
        if (mc.world == null) {
            return null;
        }

        ItemGroup.DisplayContext ctx = new ItemGroup.DisplayContext(mc.world.getEnabledFeatures(), true, mc.world.getRegistryManager());

        Registries.ITEM_GROUP.stream().filter(group -> group.getType() == ItemGroup.Type.CATEGORY).forEach(group -> group.updateEntries(ctx));
        Registries.ITEM_GROUP.stream().filter(group -> group.getType() == ItemGroup.Type.SEARCH).forEach(group -> group.updateEntries(ctx));

        return ctx;
    }

    private static ItemGroup.DisplayContext displayContext;
    private static boolean loadedPinyinData;
}
