package com.github.Debris.DebrisClient.inventory;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class QuickBundle {
    public static boolean tryClearBundle() {
        ItemStack heldStack = InventoryUtil.getHeldStack();

        if (heldStack.isEmpty()) {// empty hand, try clear cursor bundle
            Optional<Slot> slotMouseOver = InventoryUtil.getSlotMouseOver();
            if (slotMouseOver.isPresent()) {
                Slot slot = slotMouseOver.get();
                if (ItemUtil.isBundle(slot.getStack())) {
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
        if (slot.getStack().isEmpty()) InventoryUtil.leftClick(slot);// put down
    }
}
