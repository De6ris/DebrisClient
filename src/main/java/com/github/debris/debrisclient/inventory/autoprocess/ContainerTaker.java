package com.github.debris.debrisclient.inventory.autoprocess;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.util.InventoryUtil;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class ContainerTaker implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerTaker.getBooleanValue();
    }

    @Override
    public ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory) {
        List<Slot> list = containerSection.streamNotEmpty().toList();
        list.forEach(InventoryUtil::quickMove);

        if (containerSection.isEmpty()) {
            if (AutoProcessManager.allowMessage()) {
                InfoUtils.printActionbarMessage("debris_client.auto_processor.container_taker.message", InventoryUtil.getGuiContainer().getTitle(), list.size());
            }
            return ProcessResult.CLOSE_TERMINATE;
        } else {
            return ProcessResult.OPEN_TERMINATE;
        }
    }
}
