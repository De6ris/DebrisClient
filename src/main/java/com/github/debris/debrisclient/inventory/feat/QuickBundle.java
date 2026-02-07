package com.github.debris.debrisclient.inventory.feat;

import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class QuickBundle {
    public static boolean tryClearBundle() {
        ItemStack heldStack = InventoryUtil.getHeldStack();

        if (heldStack.isEmpty()) {// empty hand, try clear cursor bundle
            Optional<Slot> slotMouseOver = InventoryUtil.getHoveredSlot();
            if (slotMouseOver.isPresent()) {
                Slot slot = slotMouseOver.get();
                if (ItemUtil.isBundle(slot.getItem())) {
                    clearCursorBundle(slot);
                    return true;
                }
            }
            return false;
        }

        if (ItemUtil.isBundle(heldStack)) {
            Optional<ContainerSection> sectionOptional = SectionHandler.getSectionMouseOver();
            if (sectionOptional.isPresent()) {
                clearHeldBundle(sectionOptional.get());
                return true;
            }
        }

        return false;
    }

    private static void clearHeldBundle(ContainerSection section) {
        section.emptyRun(InventoryUtil::rightClick);
    }

    private static void clearCursorBundle(Slot slot) {
        InventoryUtil.leftClick(slot);// take up
        ContainerSection section = SectionHandler.getSection(slot);
        clearHeldBundle(section);
        if (slot.getItem().isEmpty()) InventoryUtil.leftClick(slot);// put down
    }
}
