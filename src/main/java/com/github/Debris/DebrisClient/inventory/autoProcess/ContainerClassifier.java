package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ContainerClassifier implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerClassifier.getBooleanValue();
    }

    @Override
    public ProcessResult process() {
        Optional<ContainerSection> optional = InventoryTweaks.getChestSection();
        if (optional.isEmpty()) {
            InfoUtils.printActionbarMessage("自动向容器归类: 不支持的容器!");
            return ProcessResult.SKIP;
        }

        ContainerSection section = optional.get();
        Set<Item> samples = section.slots().stream().map(Slot::getStack).map(ItemStack::getItem).collect(Collectors.toSet());

        EnumSection.InventoryWhole.get().predicateRun(x -> samples.contains(x.getItem()), InventoryUtil::quickMove);

        if (section.isFull()) {
            return ProcessResult.OPEN_TERMINATE;
        } else {
            return ProcessResult.CLOSE_TERMINATE;
        }
    }
}
