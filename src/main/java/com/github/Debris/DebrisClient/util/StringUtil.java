package com.github.Debris.DebrisClient.util;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.GuiBase;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringUtil {
    public static List<String> getConfigOptionListHoverString(IConfigOptionList config) {
        IConfigOptionListEntry defaultEntry = config.getDefaultOptionListValue();
        ImmutableList.Builder<IConfigOptionListEntry> builder = ImmutableList.builder();
        builder.add(defaultEntry);
        IConfigOptionListEntry next = defaultEntry.cycle(true);
        while (next != defaultEntry) {
            builder.add(next);
            next = next.cycle(true);
        }
        ImmutableList<IConfigOptionListEntry> entries = builder.build();

        IConfigOptionListEntry currentEntry = config.getOptionListValue();
        List<String> hover = new ArrayList<>();
        hover.add("可用值:");
        for (IConfigOptionListEntry entry : entries) {
            if (entry == defaultEntry) {
                hover.add(GuiBase.TXT_AQUA + entry.getDisplayName() + "<--默认值");
            } else if (entry == currentEntry) {
                hover.add(GuiBase.TXT_GREEN + entry.getDisplayName() + "<--当前值");
            } else {
                hover.add(entry.getDisplayName());
            }
        }
        return hover;
    }

    public static boolean isModLoadedWithNewEnoughVersion(String modId, String leastVersion) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isEmpty()) return false;
        Version version = optional.get().getMetadata().getVersion();
        try {
            Version parse = Version.parse(leastVersion);
            if (version.compareTo(parse) >= 0) return true;
        } catch (VersionParsingException e) {
            return false;
        }
        return false;
    }
}
