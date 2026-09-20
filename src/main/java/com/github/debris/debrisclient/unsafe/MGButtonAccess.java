package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.DebrisClient;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

public class MGButtonAccess {
//    public static void reload() {
//        ConfigManager.init();
//    }
//
//    public static void findScrollBarAndRun(GuiDescription description, Consumer<WScrollBar> action) {
//        if (description instanceof CommandGUI commandGUI) {
//            List<? extends CommandListPanel<?, ?>> listPanels = commandGUI.getRootPanel().streamChildren()
//                    .filter(x -> x instanceof CommandListPanel<?, ?>)
//                    .map(x -> (CommandListPanel<?, ?>) x)
//                    .toList();
//            if (listPanels.size() != 1) {
//                DebrisClient.logger.warn("Mixin CottonClientScreen: Why CommandGUI contains 0 or >1 list panel");
//            } else {
//                action.accept(listPanels.getFirst().getScrollBar());
//            }
//        }
//    }

    public static void reload() {
        try {
            Class<?> clazz = Class.forName("work.msdnicrosoft.commandbuttons.data.ConfigManager");
            Method method = clazz.getDeclaredMethod("init");
            method.invoke(null);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            DebrisClient.logger.warn("reloading mg buttons", e);
        }
    }

    public static void findScrollBarAndRun(GuiDescription description, Consumer<WScrollBar> action) {
        try {
            Class<?> clazzCommandGUI = Class.forName("work.msdnicrosoft.commandbuttons.gui.CommandGUI");
            Class<?> clazzCommandListPanel = Class.forName("work.msdnicrosoft.commandbuttons.gui.CommandListPanel");

            if (clazzCommandGUI.isInstance(description)) {
                List<WWidget> listPanels = description.getRootPanel().streamChildren()
                        .filter(clazzCommandListPanel::isInstance)
                        .toList();
                if (listPanels.size() != 1) {
                    DebrisClient.logger.warn("Mixin CottonClientScreen: Why CommandGUI contains 0 or >1 list panel");
                } else {
                    WWidget first = listPanels.getFirst();
                    Method method = clazzCommandListPanel.getMethod("getScrollBar");
                    action.accept((WScrollBar) method.invoke(first));
                }
            }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            DebrisClient.logger.warn("find mg buttons scroll bar", e);
        }
    }
}
