package com.github.debris.debrisclient.inventory.sort;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.PinYinSupport;
import com.github.debris.debrisclient.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Comparator;

public enum SortCategory {
    CREATIVE_INVENTORY(SortCategory::compareByCreativeInventory),
    TRANSLATION_KEY(Comparator.comparing(BuiltInRegistries.ITEM::getKey)),
    TRANSLATION_RESULT(Comparator.comparing(StringUtil::translateItem)),
    PINYIN(SortCategory::compareByPinyin);

    private final Comparator<Item> order;// this assumes they are distinct

    SortCategory(Comparator<Item> order) {
        this.order = order;
    }

    private static final Comparator<Item> FALLBACK = TRANSLATION_KEY.order;

    /*
     * When using, if result > 0, I will swap.
     * Thus, if you want a comes before b, you should let a be smaller than b in the comparator.
     * */
    public static Comparator<ItemStack> getItemStackSorter() {
        Comparator<Item> itemOrderByConfig = DCCommonConfig.ItemSortingOrder.getEnumValue().order;
        Comparator<ItemStack> itemTypeComparator = (c1, c2) -> {
            if (ItemStack.isSameItem(c1, c2)) {
                return 0;
            }
            return itemOrderByConfig.compare(c1.getItem(), c2.getItem());
        };

        return itemTypeComparator
                .thenComparing(ItemStackComparators.COUNT.reversed())// large stacks come first
                .thenComparing(ItemStackComparators.SHULKER_BOX)
                .thenComparing(ItemStackComparators.BUNDLE)
                .thenComparing(ItemStackComparators.ENCHANTMENT.reversed())// more enchantments come first
                .thenComparing(ItemStackComparators.DAMAGE)// here damage is lost durability, so lossless items come first
                .thenComparing(x -> x.getHoverName().getString())
                ;
    }

    private static int compareByCreativeInventory(Item c1, Item c2) {
        CreativeModeTab searchGroup = CreativeModeTabs.searchTab();
        Collection<ItemStack> displayStacks = searchGroup.getDisplayItems();
        if (displayStacks.isEmpty()) {
            buildDisplayContext(Minecraft.getInstance());
            displayStacks = searchGroup.getDisplayItems();
        }
        for (ItemStack displayStack : displayStacks) {
            Item item = displayStack.getItem();
            if (item == c1) return -1;
            if (item == c2) return 1;
        }
        return FALLBACK.compare(c1, c2);
    }

    private static int compareByPinyin(Item c1, Item c2) {
        if (PinYinSupport.available()) {
            String translate1 = StringUtil.translateItem(c1);
            String translate2 = StringUtil.translateItem(c2);
            return PinYinSupport.compareString(translate1, translate2, () -> FALLBACK.compare(c1, c2));
        }

        return FALLBACK.compare(c1, c2);
    }

    private static void buildDisplayContext(Minecraft mc) {
        if (mc.level == null) {
            return;
        }

        CreativeModeTab.ItemDisplayParameters ctx = new CreativeModeTab.ItemDisplayParameters(mc.level.enabledFeatures(), true, mc.level.registryAccess());
        BuiltInRegistries.CREATIVE_MODE_TAB.stream().filter(group -> group.getType() == CreativeModeTab.Type.CATEGORY).forEach(group -> group.buildContents(ctx));
        CreativeModeTabs.searchTab().buildContents(ctx);
    }
}
