package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.gui.DCConfigUi;
import com.github.debris.debrisclient.gui.UniversalSearchScreen;
import com.github.debris.debrisclient.unsafe.MiniHudAccess;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import com.github.debris.debrisclient.util.AccessorUtil;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ConfigCollector {
    private static final Logger LOGGER = LogManager.getLogger(ConfigCollector.class);

    private static boolean initialized = false;
    private static Map<GuiConfigsBase.ConfigOptionWrapper, Source> SOURCE_MAP = Map.of();
    private static List<GuiConfigsBase.ConfigOptionWrapper> ALL_CONFIGS = List.of();

    public static Map<GuiConfigsBase.ConfigOptionWrapper, Source> getSourceMap() {
        return SOURCE_MAP;
    }

    public static List<GuiConfigsBase.ConfigOptionWrapper> getAllConfigs() {
        return ALL_CONFIGS;
    }

    public static void bootStrap() {
        if (!initialized) {
            build();
            initialized = true;
        }
    }

    private static void build() {
        SequencedMap<Source, List<GuiConfigsBase.ConfigOptionWrapper>> configMap = buildConfigMap(Registry.CONFIG_SCREEN.getAllModsWithConfigScreens());
        SOURCE_MAP = buildSourceMap(configMap);
        ALL_CONFIGS = buildConfigList(configMap);
    }

    public static SequencedMap<Source, List<GuiConfigsBase.ConfigOptionWrapper>> buildConfigMap(ImmutableList<ModInfo> mods) {
        LinkedHashMap<Source, List<GuiConfigsBase.ConfigOptionWrapper>> map = new LinkedHashMap<>();
        for (ModInfo mod : mods) {
            if (shouldSkipMod(mod)) continue;

            Supplier<GuiBase> supplier = mod.getConfigScreenSupplier();
            if (supplier == null) continue;
            GuiBase guiBase = supplier.get();
            if (!(guiBase instanceof GuiConfigsBase guiConfigsBase)) continue;

            try {
                collect(mod, guiConfigsBase, map::put);
            } catch (RuntimeException e) {
                LOGGER.warn("exception while collecting configs for {}", mod, e);
            }
        }
        return map;
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean shouldSkipMod(ModInfo mod) {
        if (mod == UniversalSearchScreen.Instance) return true;
        return false;
    }

    /**
     * The buttons' actions may lead to other screens.
     */
    private static void collect(ModInfo mod, GuiConfigsBase gui, BiConsumer<Source, List<GuiConfigsBase.ConfigOptionWrapper>> collector) {
        String modId = mod.getModId();
        String modName = mod.getModName();

        processGui(mod, gui);
        gui.initGui();//add buttons

        for (ButtonBase button : AccessorUtil.getButtons(gui).toArray(new ButtonBase[0])) {
            if (shouldSkipButton(mod, gui, button)) continue;

            IButtonActionListener listener = AccessorUtil.getActionListener(button);
            if (listener == null) continue;

            listener.actionPerformedWithButton(button, 0);// switch to this tab
            List<GuiConfigsBase.ConfigOptionWrapper> configs = gui.getConfigs();
            if (configs.isEmpty()) continue;

            collector.accept(new Source(modId, modName, AccessorUtil.getDisplayString(button)), configs);
        }
    }

    private static void processGui(ModInfo mod, GuiConfigsBase gui) {
        String modId = mod.getModId();
        switch (modId) {
            case ModReference.Litematica -> LitematicaAccessor.resetTab();
            case ModReference.MiniHud -> MiniHudAccess.resetTab();
        }
    }

    private static boolean shouldSkipButton(ModInfo mod, GuiConfigsBase gui, ButtonBase button) {
        String content = AccessorUtil.getDisplayString(button);
        String modId = mod.getModId();
        switch (modId) {
            case DebrisClient.MOD_ID -> {
                if (content.equals(DCConfigUi.Tab.ALL.getDisplayName())) return true;
                if (content.contains("全部")) return true;
            }
            case ModReference.Litematica -> {
                if (LitematicaAccessor.isRenderLayerButton(content)) return true;
            }
            case ModReference.MiniHud -> {
                if (MiniHudAccess.isShapeButton(content)) return true;
            }
        }
        return false;
    }

    public static Map<GuiConfigsBase.ConfigOptionWrapper, Source> buildSourceMap(Map<Source, List<GuiConfigsBase.ConfigOptionWrapper>> configMap) {
        Map<GuiConfigsBase.ConfigOptionWrapper, Source> map = new HashMap<>();
        for (Map.Entry<Source, List<GuiConfigsBase.ConfigOptionWrapper>> entry : configMap.entrySet()) {
            Source source = entry.getKey();
            List<GuiConfigsBase.ConfigOptionWrapper> list = entry.getValue();
            for (GuiConfigsBase.ConfigOptionWrapper wrapper : list) {
                map.putIfAbsent(wrapper, source);// may overlap
            }
        }
        return map;
    }

    private static List<GuiConfigsBase.ConfigOptionWrapper> buildConfigList(Map<Source, List<GuiConfigsBase.ConfigOptionWrapper>> configMap) {
        return configMap.values().stream().flatMap(Collection::stream)
                .collect(
                        Collectors.toMap(GuiConfigsBase.ConfigOptionWrapper::getConfig,
                                UnaryOperator.identity(),
                                (x, y) -> x,
                                LinkedHashMap::new)
                )
                .values()
                .stream()
                .toList();
    }

    public record Source(String modId, String modName, String tab) {
    }
}
