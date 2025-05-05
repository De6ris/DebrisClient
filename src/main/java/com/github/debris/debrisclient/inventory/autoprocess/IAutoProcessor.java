package com.github.debris.debrisclient.inventory.autoprocess;

import com.github.debris.debrisclient.inventory.section.ContainerSection;

public interface IAutoProcessor {
    boolean isActive();

    ProcessResult process(ContainerSection containerSection, ContainerSection playerInventory);
}
