package com.github.Debris.DebrisClient.inventory.sort;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.PinYinSupport;
import com.github.Debris.DebrisClient.util.StringUtil;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.Collection;
import java.util.Comparator;

public enum SortCategory implements IConfigOptionListEntry {
    CREATIVE_INVENTORY("creative_inventory", "创造模式物品栏", SortCategory::compareByCreativeInventory),
    TRANSLATION_KEY("translation_key", "翻译键", Comparator.comparing(Registries.ITEM::getId)),
    TRANSLATION_RESULT("translation_result", "翻译结果", Comparator.comparing(StringUtil::translateItem)),
    PINYIN("pinyin", "拼音(需要Rei)", SortCategory::compareByPinyin);

    private final String configString;
    private final String translationKey;
    private final Comparator<Item> order;// this assumes they are distinct

    SortCategory(String configString, String translationKey, Comparator<Item> order) {
        this.configString = configString;
        this.translationKey = translationKey;
        this.order = order;
    }

    public static SortCategory getCategory() {
        return (SortCategory) DCCommonConfig.ItemSortingOrder.getOptionListValue();
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
                .thenComparing(ItemStackComparators.BUNDLE)
                .thenComparing(ItemStackComparators.ENCHANTMENT.reversed())// more enchantments come first
                .thenComparing(ItemStackComparators.DAMAGE)// here damage is lost durability, so lossless items come first
                .thenComparing(x -> x.getName().getString())
                ;
    }

    private static void setup(SortCategory category) {
        if (category == SortCategory.CREATIVE_INVENTORY) {
            if (displayContext == null) {
                displayContext = buildDisplayContext(MinecraftClient.getInstance());
            }// this make the collection non-empty
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
        if (PinYinSupport.available()) {
            String translate1 = StringUtil.translateItem(c1);
            String translate2 = StringUtil.translateItem(c2);
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

    @Override
    public String getStringValue() {
        return this.configString;
    }

    @Override
    public String getDisplayName() {
        return this.translationKey;
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        if (forward) {
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }
        return values()[id % values().length];
    }

    @Override
    public IConfigOptionListEntry fromString(String name) {
        return fromStringStatic(name);
    }

    public static SortCategory fromStringStatic(String name) {
        for (SortCategory val : values()) {
            if (val.configString.equalsIgnoreCase(name)) {
                return val;
            }
        }
        return SortCategory.CREATIVE_INVENTORY;
    }
}
