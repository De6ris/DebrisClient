package com.github.Debris.DebrisClient.inventory.autoProcess;

public interface IAutoProcessor {
    boolean isActive();

    ProcessResult process();
}
