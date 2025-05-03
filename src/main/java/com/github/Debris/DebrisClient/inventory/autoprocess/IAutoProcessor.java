package com.github.Debris.DebrisClient.inventory.autoprocess;

import com.github.Debris.DebrisClient.inventory.section.ContainerSection;

public interface IAutoProcessor {
    boolean isActive();

    ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory);
}
