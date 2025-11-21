package com.github.debris.debrisclient.gui;

import com.github.debris.debrisclient.feat.ConfigCollector;
import com.github.debris.debrisclient.util.AccessorUtil;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;

import java.util.List;

public class UniversalSearchScreen extends GuiConfigsBase {
    public static final ModInfo Instance = new ModInfo("universal_search", "Universal Search", UniversalSearchScreen::new);

    public UniversalSearchScreen() {
        super(10, 50, "universal_search", null, "全局搜索");
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigCollector.getAllConfigs();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void initGui() {
        ConfigCollector.bootStrap();
        if (this.mc.screen != this) this.mc.setScreen(this);// may go to other screens while collecting configs
        super.initGui();
        WidgetListConfigOptions listWidget = this.getListWidget();
        AccessorUtil.setAllowKeyboardNavigation(listWidget, true);
        WidgetSearchBar searchBar = listWidget.getSearchBarWidget();
        if (searchBar != null) searchBar.setSearchOpen(true);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks) {
        super.render(drawContext, mouseX, mouseY, partialTicks);
        this.renderConfigSource(drawContext);
    }

    private void renderConfigSource(GuiGraphics drawContext) {
        WidgetListConfigOptions listWidget = this.getListWidget();
        WidgetBase hoveredWidget = AccessorUtil.getHoveredWidget(listWidget);
        if (hoveredWidget instanceof WidgetConfigOption widget) {
            ConfigOptionWrapper wrapper = AccessorUtil.getWrapper(widget);
            ConfigCollector.Source source = ConfigCollector.getSourceMap().get(wrapper);
            if (source != null) {
                MutableComponent text = Component.literal("来源").withColor(CommonColors.GREEN)
                        .append(Component.literal(": ").withColor(CommonColors.WHITE))
                        .append(Component.literal(source.modName()).withColor(CommonColors.HIGH_CONTRAST_DIAMOND))
                        .append(Component.literal("-").withColor(CommonColors.WHITE))
                        .append(Component.literal(source.tab()).withColor(CommonColors.YELLOW));
                drawContext.drawString(this.font, text, 20, 35, CommonColors.WHITE, false);
            }
        }
    }

}
