package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.GuiBase;

import java.util.ArrayList;
import java.util.List;

public class EnhanceConfig {
    public static boolean isActive() {
        return DCCommonConfig.GlobalConfigEnhance.getBooleanValue();
    }

    public static List<String> createOptionListTooltip(IConfigOptionList config) {
        IConfigOptionListEntry defaultEntry = config.getDefaultOptionListValue();
        return mapTooltip(config, collectAllEntries(defaultEntry), defaultEntry);
    }

    private static List<IConfigOptionListEntry> collectAllEntries(IConfigOptionListEntry defaultEntry) {
        List<IConfigOptionListEntry> list = new ArrayList<>();
        list.add(defaultEntry);
        IConfigOptionListEntry next = defaultEntry.cycle(true);
        while (next != defaultEntry) {
            list.add(next);
            next = next.cycle(true);
        }
        return list;
    }

    private static List<String> mapTooltip(IConfigOptionList config, List<IConfigOptionListEntry> entries, IConfigOptionListEntry defaultEntry) {
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
}
