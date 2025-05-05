package com.github.debris.debrisclient.inventory.autoprocess;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.util.InventoryUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContainerClassifier implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerClassifier.getBooleanValue();
    }

    @Override
    public ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory) {
        Set<Item> samples = containerSection.slots().stream().map(Slot::getStack).map(ItemStack::getItem).collect(Collectors.toSet());

        List<Slot> list = playerInventory.predicate(x -> samples.contains(x.getItem())).toList();
        list.forEach(InventoryUtil::quickMove);

        if (containerSection.isFull()) {
            return ProcessResult.OPEN_TERMINATE;
        } else {
            if (AutoProcessManager.allowMessage()) {
                InfoUtils.printActionbarMessage("debris_client.auto_processor.container_classifier.message", InventoryUtil.getGuiContainer().getTitle(), list.size());
            }
            return ProcessResult.CLOSE_TERMINATE;
        }
    }
}
