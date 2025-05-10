package com.github.debris.debrisclient.inventory.autoprocess;

import com.github.debris.debrisclient.inventory.feat.ContainerTemplate;
import com.github.debris.debrisclient.feat.interactor.BlockInteractor;
import com.github.debris.debrisclient.feat.interactor.EntityInteractor;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

import java.util.List;

public class AutoProcessManager {
    private static final List<IAutoProcessor> PROCESSORS = ImmutableList.of(
            new ContainerTaker(),
            new ItemFinder(),
            new ContainerTemplate.Recorder(),
            new ContainerTemplate.Filler()
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

        ContainerSection containerSection = EnumSection.Container.get();
        ContainerSection playerInventory = EnumSection.InventoryWhole.get();

        for (IAutoProcessor processor : PROCESSORS) {
            if (!processor.isActive()) continue;
            ProcessResult result = processor.process(containerSection, playerInventory);
            closeGui |= result.closeGui();
            if (result.terminate()) break;
        }

        if (closeGui) {
            screen.close();
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean allowMessage() {
        if (BlockInteractor.INSTANCE.hasPending() || EntityInteractor.INSTANCE.hasPending()) return false;
        return true;
    }

    private static boolean hasActiveProcessor() {
        return PROCESSORS.stream().anyMatch(IAutoProcessor::isActive);
    }

}
