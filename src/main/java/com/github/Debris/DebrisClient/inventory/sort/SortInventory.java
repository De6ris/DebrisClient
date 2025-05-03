package com.github.Debris.DebrisClient.inventory.sort;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.ItemUtil;
import com.github.Debris.DebrisClient.util.PermutationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class SortInventory {
    public static boolean trySort() {
        Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
        if (optional.isEmpty()) return false;
        ContainerSection section = optional.get();
        if (!shouldSort(section)) return false;
        int before = InventoryUtil.getChangeCount();
        InventoryTweaks.makeSureNotHoldingItem(section);
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

        Comparator<Slot> slotSorter = (x, y) -> itemStackSorter.compare(x.getStack(), y.getStack());

        BiConsumer<Slot, Slot> swapAction = InventoryUtil::swapSlots;

        if (DCCommonConfig.SortingContainersLast.getBooleanValue()) {
            putContainersLast(section);

            Map<Boolean, List<Slot>> grouped = section.slots().stream().filter(Slot::hasStack).collect(Collectors.partitioningBy(x -> ItemUtil.isContainer(x.getStack())));

            Slot[] nonContainers = grouped.get(false).toArray(Slot[]::new);
            Slot[] containers = grouped.get(true).toArray(Slot[]::new);

            runSorting(nonContainers, slotSorter, swapAction);
            runSorting(containers, slotSorter, swapAction);
        } else {
            section.fillBlanks();
            section.mergeSlots();
            section.fillBlanks();
            Slot[] nonEmptySlots = section.slots().stream().filter(Slot::hasStack).toArray(Slot[]::new);
            runSorting(nonEmptySlots, slotSorter, swapAction);
        }
    }

    private static void putContainersLast(ContainerSection section) {
        List<Slot> slots = section.slots();
        int iterateStart = slots.size() - 1;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            if (i == iterateStart) continue;// skip the first
            Slot slot = slots.get(i);
            if (!ItemUtil.isContainer(slot.getStack())) continue;// skip those not container
            moveToNextNonContainer(section, i, slot);
        }

        // now fill in the blanks
        int theIndexNonContainer = iterateStart;
        for (int i = iterateStart; i >= 0; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (ItemUtil.isContainer(slot.getStack())) continue;// skip those containers
            theIndexNonContainer = i;
            break;
        }
        ContainerSection theFormerPart = section.subSection(0, theIndexNonContainer + 1);
        theFormerPart.fillBlanks();
        theFormerPart.mergeSlots();
        theFormerPart.fillBlanks();
    }

    private static void moveToNextNonContainer(ContainerSection section, int index, Slot currentSlot) {
        List<Slot> slots = section.slots();
        for (int i = slots.size() - 1; i > index; i--) {// inverse order reduce operations
            Slot slot = slots.get(i);
            if (ItemUtil.isContainer(slot.getStack())) continue;// skip those containers
            if (slot.hasStack()) {
                InventoryUtil.swapSlots(slot, currentSlot);
            } else {
                InventoryUtil.moveToEmpty(currentSlot, slot);
            }
            break;
        }
    }

    /*
     * sorter: if j is bigger than j+1, I will swap them
     * */
    private static <T> void runSorting(T[] slots, Comparator<T> sorter, BiConsumer<T, T> swapAction) {
        int length = slots.length;
        if (length <= 1) return;

        if (DCCommonConfig.CachedSorting.getBooleanValue()) {
            List<PermutationUtil.Transposition> optimal = PermutationUtil.getOptimalProcess(slots, sorter);
            optimal.forEach(x -> x.operate(slots, swapAction));
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
