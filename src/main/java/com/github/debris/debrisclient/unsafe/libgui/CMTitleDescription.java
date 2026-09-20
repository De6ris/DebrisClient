package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class CMTitleDescription extends LightweightGuiDescription {
    private static final int BUTTON_WIDTH = 40;
    private static final int BUTTON_HEIGHT = 20;

    private final WPlainPanel root = new WPlainPanel();

    private final WButton generatorButton = new TooltipButton(Component.literal("生成器"));
    private final WButton cancelButton = new TooltipButton(Component.literal("取消"));

    public CMTitleDescription() {
        this.setupRoot(this.root);
        this.setRootPanel(this.root);
    }

    @Override
    public void addPainters() {
        this.rootPanel.setBackgroundPainter(BackgroundPainter.createColorful(1291845632));
    }

    private void setupRoot(WPlainPanel root) {
        int xSize = 324;
        int ySize = 192;

        root.setSize(xSize, ySize);
        root.setInsets(Insets.ROOT_PANEL);


        root.add(this.generatorButton, 0, ySize - 60, BUTTON_WIDTH, BUTTON_HEIGHT);
        root.add(this.cancelButton, 50, ySize - 60, BUTTON_WIDTH, BUTTON_HEIGHT);

        this.generatorButton.setOnClick(() -> Minecraft.getInstance().setScreenAndShow(CMGeneratorScreen.INSTANCE));
        this.cancelButton.setOnClick(() -> Minecraft.getInstance().setScreenAndShow(null));

        root.validate(this);
    }

}
