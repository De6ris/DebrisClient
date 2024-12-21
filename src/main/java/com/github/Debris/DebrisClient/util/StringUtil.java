package com.github.Debris.DebrisClient.util;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.GuiBase;

import java.util.ArrayList;
import java.util.List;

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
}
