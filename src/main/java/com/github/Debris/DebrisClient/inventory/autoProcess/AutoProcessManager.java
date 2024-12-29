package com.github.Debris.DebrisClient.inventory.autoProcess;

import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.List;

public class AutoProcessManager {
    private static final List<IAutoProcessor> PROCESSORS = ImmutableList.of(
            new ContainerTaker(),
            new ItemFinder()
    );

    public static void onGuiContainerOpen(HandledScreen<?> screen) {
        ((IAutoProcessScreen) screen).dc$setShouldProcess(true);
    }

    @SuppressWarnings("ConstantConditions")
    public static void onContainerUpdate(ScreenHandler container) {
        if (!hasActiveProcessor()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (Predicates.notInGuiContainer(client)) return;
        HandledScreen<?> screen = InventoryUtil.getGuiContainer();
        if (InventoryUtil.getContainer(screen) != container) return;

        if (!((IAutoProcessScreen) screen).dc$shouldProcess()) return;
        ((IAutoProcessScreen) screen).dc$setShouldProcess(false);

        boolean closeGui = false;

        for (IAutoProcessor processor : PROCESSORS) {
            if (!processor.isActive()) continue;
            ProcessResult result = processor.process();
            closeGui |= result.closeGui();
            if (result.terminate()) break;
        }

        if (closeGui) {
            screen.close();
        }
    }

    private static boolean hasActiveProcessor() {
        return PROCESSORS.stream().anyMatch(IAutoProcessor::isActive);
    }

}
