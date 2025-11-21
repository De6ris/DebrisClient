package com.github.debris.debrisclient.inventory.section;

import com.github.debris.debrisclient.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SectionHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<ContainerSection> unidentifiedSections = new ArrayList<>();
    private final Map<EnumSection, ContainerSection> sectionMap = new EnumMap<>(EnumSection.class);

    public SectionHandler(AbstractContainerScreen<?> guiContainer) {
        this.identifyContainer(guiContainer, guiContainer.getMenu());
    }

    public SectionHandler(AbstractContainerMenu container) {
        this.identifyContainer(null, container);
    }

    private void identifyContainer(@Nullable AbstractContainerScreen<?> guiContainer, AbstractContainerMenu container) {
        if (container == null) {
            LOGGER.warn("null container while identifying screen {} ", guiContainer);
            return;
        }

        List<Slot> slots = InventoryUtil.getSlots(container);
        Map<Container, List<Slot>> groupedByInventory = slots.stream().collect(Collectors.groupingBy(x -> x.container));
        SectionIdentifier identifier = new SectionIdentifier(this::putSection, this::handleUnidentified);
        groupedByInventory.forEach((iInventory, partSlots) -> identifier.identify(guiContainer, container, iInventory, partSlots));
    }

    private void putSection(EnumSection key, ContainerSection section) {
        Map<EnumSection, ContainerSection> sectionMap = this.sectionMap;
        if (sectionMap.containsKey(key)) {
            LOGGER.warn("duplicate section for key {}: {} replacing {}", key, sectionMap.get(key), section);
        }
        sectionMap.put(key, section);
    }

    private void handleUnidentified(ContainerSection section) {
        this.sectionMap.putIfAbsent(EnumSection.Unidentified, section);
        this.unidentifiedSections.add(section);
    }

    public static void onClientPlayerInit(InventoryMenu playerContainer) {
        SectionHandler sectionHandler = new SectionHandler(playerContainer);
        ((IContainer) playerContainer).dc$setSectionHandler(sectionHandler);
    }

    public static void markDirty(AbstractContainerScreen<?> guiContainer) {
        AbstractContainerMenu container = guiContainer.getMenu();
        ((IContainer) container).dc$setSectionHandler(null);
    }

    public static SectionHandler getSectionHandler() {
        AbstractContainerMenu container = InventoryUtil.getCurrentContainer();
        SectionHandler sectionHandler = ((IContainer) container).dc$getSectionHandler();
        if (sectionHandler != null) return sectionHandler;

        Screen screen = GuiUtils.getCurrentScreen();
        if (!(screen instanceof AbstractContainerScreen<?> guiContainer)) {
            LOGGER.warn("weird that in a non container screen with non-default container, screen:\n{}", screen);
            return ((IContainer) InventoryUtil.getInventoryContainer()).dc$getSectionHandler();
        }

        SectionHandler newHandler = new SectionHandler(guiContainer);
        ((IContainer) container).dc$setSectionHandler(newHandler);
        return newHandler;
    }

    public static ContainerSection getSection(EnumSection section) {
        ContainerSection ret = getSectionHandler().sectionMap.get(section);
        if (ret == null) {
            LOGGER.warn("no section instance for {}", section);
            return ContainerSection.EMPTY;
        }
        return ret;
    }

    public static boolean hasSection(EnumSection section) {
        return getSectionHandler().sectionMap.containsKey(section);
    }

    public static List<ContainerSection> getUnIdentifiedSections() {
        return getSectionHandler().unidentifiedSections;
    }

    /**
     * The unidentified may appear both in the list and the map, so use distinct here.
     */
    public static Stream<ContainerSection> streamAllSections() {
        SectionHandler sectionHandler = getSectionHandler();
        return Stream.concat(sectionHandler.unidentifiedSections.stream(), sectionHandler.sectionMap.values().stream()).distinct();
    }

    public static Optional<ContainerSection> getSectionMouseOver() {
        Optional<Slot> slotMouseOver = InventoryUtil.getSlotMouseOver();
        return slotMouseOver.map(SectionHandler::getSection);
    }

    public static ContainerSection getSection(Slot slot) {
        return streamAllSections().filter(x -> x.hasSlot(slot)).findFirst().orElse(ContainerSection.EMPTY);
    }

    public static ContainerSection getSection(int globalIndex) {
        return streamAllSections()
                .filter(x -> x.slots()
                        .stream()
                        .anyMatch(y -> InventoryUtil.getSlotId(y) == globalIndex)
                )
                .findFirst()
                .orElse(ContainerSection.EMPTY);
    }
}
