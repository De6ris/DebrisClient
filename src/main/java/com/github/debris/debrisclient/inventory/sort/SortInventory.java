package com.github.debris.debrisclient.inventory.sort;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SortInventory {
    public static boolean trySort() {
        Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
        if (optional.isEmpty()) return false;
        ContainerSection section = optional.get();
        if (!shouldSort(section)) return false;
        int before = InventoryUtil.getChangeCount();
        InventoryTweaks.clearCursor(section);
        sortInternal(section);
        int after = InventoryUtil.getChangeCount();
        return after != before;// seen as sort success
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean shouldSort(ContainerSection section) {
        if (ContainerSection.isActionSection(section)) return false;
        return true;
    }

    // assume no holding item, all slots are well merged, but still blanks between
    private static void sortInternal(ContainerSection section) {
        Comparator<ItemStack> itemStackSorter = SortCategory.getItemStackSorter();
        Comparator<Slot> slotSorter = (x, y) -> itemStackSorter.compare(x.getItem(), y.getItem());
        BiConsumer<Slot, Slot> swapAction = InventoryUtil::swapSlots;

        if (DCCommonConfig.SortingContainersLast.getBooleanValue()) {
            putContainersLast(section);
            splitByContainer(section).forEach(x -> process(x, slotSorter, swapAction));
        } else {
            process(section, slotSorter, swapAction);
        }
    }

    private static void process(ContainerSection section, Comparator<Slot> sorter, BiConsumer<Slot, Slot> swapAction) {
        section.fillBlanks();
        section.mergeSlots();
        section.fillBlanks();
        Slot[] nonEmptySlots = section.slots().stream().filter(Slot::hasItem).toArray(Slot[]::new);
        runSorting(nonEmptySlots, sorter, swapAction);
    }

    private static void putContainersLast(ContainerSection section) {
        List<Slot> slots = section.slots();
        for (int index = slots.size() - 2; index >= 0; index--) {// inverse order reduce operations; skip the last
            Slot slot = slots.get(index);
            if (!ItemUtil.isContainer(slot.getItem())) continue;// skip those not container
            moveToNextNonContainer(slots, index, slot);
        }
    }

    private static void moveToNextNonContainer(List<Slot> slots, int index, Slot currentSlot) {
        for (int i = slots.size() - 1; i > index; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (ItemUtil.isContainer(slot.getItem())) continue;// skip those containers
            if (slot.hasItem()) {
                InventoryUtil.swapSlots(slot, currentSlot);
            } else {
                InventoryUtil.moveToEmpty(currentSlot, slot);
            }
            break;
        }
    }

    private static List<ContainerSection> splitByContainer(ContainerSection section) {
        List<Slot> slots = section.slots();
        int size = section.size();
        int indexNonContainer = -1;
        for (int i = size - 1; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (ItemUtil.isContainer(slot.getItem())) continue;// skip those containers
            indexNonContainer = i;
            break;
        }
        // -1 means all containers, size-1 means no containers
        ContainerSection former = section.subSection(0, indexNonContainer + 1);
        ContainerSection latter = section.subSection(indexNonContainer + 1, size);
        return List.of(former, latter);
    }

    /*
     * sorter: if j is bigger than j+1, I will swap them
     * */
    private static <T> void runSorting(T[] slots, Comparator<T> sorter, BiConsumer<T, T> swapAction) {
        int length = slots.length;
        if (length <= 1) return;

        if (DCCommonConfig.CachedSorting.getBooleanValue()) {
            Permutations.ofOptimal(slots, sorter).operate(slots, swapAction);
        } else {
            // direct sorting
            for (int i = 1; i < length; i++) {
                boolean flag = true;
                for (int j = 0; j < length - i; j++) {
                    T slotJ = slots[j];
                    T slotJ_1 = slots[j + 1];
                    int compare = sorter.compare(slotJ, slotJ_1);
                    if (compare > 0) {
                        swapAction.accept(slotJ, slotJ_1);
                        flag = false;
                    }
                }
                if (flag) break;
            }
        }
    }
}
