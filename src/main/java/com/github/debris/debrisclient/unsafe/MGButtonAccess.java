package com.github.debris.debrisclient.unsafe;

import com.github.debris.debrisclient.DebrisClient;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import work.msdnicrosoft.commandbuttons.data.ConfigManager;
import work.msdnicrosoft.commandbuttons.gui.CommandGUI;
import work.msdnicrosoft.commandbuttons.gui.CommandListPanel;

import java.util.List;
import java.util.function.Consumer;

public class MGButtonAccess {
    public static void reload() {
        ConfigManager.init();
    }

    public static void findScrollBarAndRun(GuiDescription description, Consumer<WScrollBar> action) {
        if (description instanceof CommandGUI commandGUI) {
            List<? extends CommandListPanel<?, ?>> listPanels = commandGUI.getRootPanel().streamChildren()
                    .filter(x -> x instanceof CommandListPanel<?, ?>)
                    .map(x -> (CommandListPanel<?, ?>) x)
                    .toList();
            if (listPanels.size() != 1) {
                DebrisClient.logger.warn("Mixin CottonClientScreen: Why CommandGUI contains 0 or >1 list panel");
            } else {
                action.accept(listPanels.getFirst().getScrollBar());
            }
        }
    }
}
