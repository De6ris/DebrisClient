package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import fi.dy.masa.malilib.util.InfoUtils;

import java.util.Optional;

public class ContainerTaker implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerTaker.getBooleanValue();
    }

    @Override
    public ProcessResult process() {
        Optional<ContainerSection> optional = InventoryTweaks.getChestSection();
        if (optional.isEmpty()) {
            InfoUtils.printActionbarMessage("自动从容器取出: 不支持的容器!");
            return ProcessResult.SKIP;
        }
        ContainerSection section = optional.get();
        section.notEmptyRun(InventoryUtil::quickMove);
        if (section.isEmpty()) {
            return ProcessResult.CLOSE_TERMINATE;
        } else {
            return ProcessResult.OPEN_TERMINATE;
        }
    }
}
