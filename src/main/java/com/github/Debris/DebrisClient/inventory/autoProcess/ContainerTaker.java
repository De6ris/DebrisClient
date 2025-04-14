package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;

public class ContainerTaker implements IAutoProcessor {
    @Override
    public boolean isActive() {
        return DCCommonConfig.AutoContainerTaker.getBooleanValue();
    }

    @Override
    public ProcessResult process() {
        ContainerSection section = EnumSection.Container.get();
        section.notEmptyRun(InventoryUtil::quickMove);
        if (section.isEmpty()) {
            return ProcessResult.CLOSE_TERMINATE;
        } else {
            return ProcessResult.OPEN_TERMINATE;
        }
    }
}
