package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Set;
import java.util.stream.Collectors;

public class ContainerClassifier implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerClassifier.getBooleanValue();
    }

    @Override
    public ProcessResult process() {
        ContainerSection section = EnumSection.Container.get();
        Set<Item> samples = section.slots().stream().map(Slot::getStack).map(ItemStack::getItem).collect(Collectors.toSet());

        EnumSection.InventoryWhole.get().predicateRun(x -> samples.contains(x.getItem()), InventoryUtil::quickMove);

        if (section.isFull()) {
            return ProcessResult.OPEN_TERMINATE;
        } else {
            return ProcessResult.CLOSE_TERMINATE;
        }
    }
}
