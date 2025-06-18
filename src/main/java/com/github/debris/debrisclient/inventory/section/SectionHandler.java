package com.github.debris.debrisclient.inventory.section;

import com.github.debris.debrisclient.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SectionHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<ContainerSection> unidentifiedSections = new ArrayList<>();
    private final Map<EnumSection, ContainerSection> sectionMap = new EnumMap<>(EnumSection.class);

    public SectionHandler(HandledScreen<?> guiContainer) {
        this.identifyContainer(guiContainer, guiContainer.getScreenHandler());
    }

    public SectionHandler(ScreenHandler container) {
        this.identifyContainer(null, container);
    }

    private void identifyContainer(@Nullable HandledScreen<?> guiContainer, ScreenHandler container) {
        List<Slot> slots = InventoryUtil.getSlots(container);
        Map<Inventory, List<Slot>> groupedByInventory = slots.stream().collect(Collectors.groupingBy(x -> x.inventory));
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

    public static void onClientPlayerInit(PlayerScreenHandler playerContainer) {
        SectionHandler sectionHandler = new SectionHandler(playerContainer);
        ((IContainer) playerContainer).dc$setSectionHandler(sectionHandler);
    }

    public static void updateSection(HandledScreen<?> guiContainer) {
        ScreenHandler container = guiContainer.getScreenHandler();
        ((IContainer) container).dc$setSectionHandler(new SectionHandler(guiContainer));
    }

    public static SectionHandler getSectionHandler() {
        SectionHandler sectionHandler = ((IContainer) InventoryUtil.getCurrentContainer()).dc$getSectionHandler();
        if (sectionHandler == null) {
            LOGGER.warn("section handler not set for container {}", InventoryUtil.getCurrentContainer());
            return ((IContainer) InventoryUtil.getInventoryContainer()).dc$getSectionHandler();
        }
        return sectionHandler;
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
        return streamAllSections().filter(x -> x.hasSlot(slot)).findFirst().orElseThrow();
    }

    public static ContainerSection getSection(int globalIndex) {
        return streamAllSections()
                .filter(x -> x.slots()
                        .stream()
                        .anyMatch(y -> InventoryUtil.getSlotId(y) == globalIndex)
                )
                .findFirst()
                .orElseThrow();
    }
}
