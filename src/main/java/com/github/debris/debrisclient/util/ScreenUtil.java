package com.github.debris.debrisclient.util;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.util.InputUtils;

import java.util.List;
import java.util.Optional;

public class ScreenUtil {
    public static <TYPE, WIDGET extends WidgetListEntryBase<TYPE>> Optional<WIDGET> getHoveredWidget(WidgetListBase<TYPE, WIDGET> widgetList) {
        List<WIDGET> widgets = AccessorUtil.getListWidgets(widgetList);
        for (WIDGET widget : widgets) {
            if (widget.isMouseOver(InputUtils.getMouseX(), InputUtils.getMouseY())) {
                return Optional.of(widget);
            }
        }
        return Optional.empty();
    }
}
