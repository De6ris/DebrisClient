package com.github.debris.debrisclient.unsafe.libgui;

import com.github.debris.debrisclient.feat.commandmacro.BuiltInCM;
import com.github.debris.debrisclient.feat.commandmacro.CMInputData;
import com.github.debris.debrisclient.feat.commandmacro.CMLogic;
import com.github.debris.debrisclient.feat.commandmacro.CommandMacro;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class CMGuiDescription extends LightweightGuiDescription {
    private static final int GRID_SIZE = 5;

    private static final int LINE_HEIGHT = 5;

    private static final int DESCRIPTION_Y_SHIFT = 1;
    private static final int DESCRIPTION_WIDTH = 14;

    private static final int TEXT_FIELD_WIDTH = 8;

    private static final int BUTTON_WIDTH = 8;
    private static final int BUTTON_HEIGHT = 4;


    private final SuppressivePanel root = new SuppressivePanel(GRID_SIZE);

    private final IntegerField period = new IntegerField().setText(CMLogic.DEFAULT_PERIOD);

    private final WTextField command = new WTextField(Component.literal(CMLogic.DEFAULT_COMMAND)) {
        {
            this.setMaxLength(256);
            this.setText(CMLogic.DEFAULT_COMMAND);
        }
    };
    private final TooltipButton suggestButton = new TooltipButton(Component.literal("预设"));
    private BuiltInCM commandSuggestion = BuiltInCM.SPAWN;

    private final IntegerField code1 = new IntegerField().setText(0);
    private final IntegerField code2 = new IntegerField().setText(9);

    private final YPosModeButton yPosModeButton = new YPosModeButton();
    private final IntegerField yPos = new IntegerField().setText(100);

    private final TooltipButton fillButton = new TooltipButton(Component.literal("填充"));
    private final IntegerField startX = new IntegerField(Component.literal("起点X"));
    private final IntegerField startZ = new IntegerField(Component.literal("起点Z"));
    private final IntegerField endX = new IntegerField(Component.literal("终点X"));
    private final IntegerField endZ = new IntegerField(Component.literal("终点Z"));

    private final WTextField file = new WTextField(Component.literal(CMLogic.DEFAULT_FILE)) {
        {
            {
                this.setMaxLength(128);
                this.setText(CMLogic.DEFAULT_FILE);
            }
        }
    };

    private final TooltipButton saveButton = new TooltipButton(Component.literal("保存"));
    private final TooltipButton executeButton = new TooltipButton(Component.literal("执行"));


    public CMGuiDescription() {
        this.setupRoot(this.root);
        this.root.setVisible(this.yPos, false);
        this.setRootPanel(this.root);
    }

    private void setupRoot(SuppressivePanel root) {
        root.setSize(324, 192);
        root.setInsets(Insets.ROOT_PANEL);

        int x = 0;
        int y = 0;

        y += LINE_HEIGHT;
        root.add(createText(Component.literal("时间间隔")).setTooltips(
                        Component.literal("单位为tick")
                ),
                0,
                y + DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH,
                1);
        root.add(this.period, DESCRIPTION_WIDTH, y, TEXT_FIELD_WIDTH, 1);

        y += LINE_HEIGHT;
        x = 0;
        root.add(createText(Component.literal("指令内容")).setTooltips(
                        Component.literal("以下占位符允许你动态填充内容:"),
                        Component.literal("${code}: bot编号"),
                        Component.literal("${pos}: bot坐标")
                ),
                0,
                y + DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH,
                1);
        x += DESCRIPTION_WIDTH;
        root.add(this.command, x, y, TEXT_FIELD_WIDTH * 5, 1);
        x += TEXT_FIELD_WIDTH * 5 + 1;
        this.suggestButton.setOnClick(this::suggest);
        root.add(this.suggestButton, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        y += LINE_HEIGHT;
        root.add(createText(Component.literal("bot编号")).setTooltips(
                        Component.literal("指令中出现坐标时, 会自动填充")
                ),
                0,
                y + DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH,
                1);
        root.add(this.code1, DESCRIPTION_WIDTH, y, TEXT_FIELD_WIDTH, 1);
        root.add(this.code2, DESCRIPTION_WIDTH + TEXT_FIELD_WIDTH + 1, y, TEXT_FIELD_WIDTH, 1);

        y += LINE_HEIGHT;
        x = 0;
        root.add(createText(Component.literal("botXZ坐标范围")).setTooltips(
                        Component.literal("可通过litematica选区信息填充")
                ),
                x,
                y + DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH,
                1);
        x += DESCRIPTION_WIDTH;
        this.fillButton.setOnClick(this::fill);
        root.add(this.fillButton, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        x += BUTTON_WIDTH + 1;
        root.add(this.startX, x, y, TEXT_FIELD_WIDTH, 1);
        x += TEXT_FIELD_WIDTH + 1;
        root.add(this.startZ, x, y, TEXT_FIELD_WIDTH, 1);
        x += TEXT_FIELD_WIDTH + 1;
        root.add(this.endX, x, y, TEXT_FIELD_WIDTH, 1);
        x += TEXT_FIELD_WIDTH + 1;
        root.add(this.endZ, x, y, TEXT_FIELD_WIDTH, 1);

        y += LINE_HEIGHT;
        root.add(createText(Component.literal("botY坐标")), 0, y + DESCRIPTION_Y_SHIFT, DESCRIPTION_WIDTH, 1);
        this.yPosModeButton.setOnClick(this::toggleYPosMode);
        root.add(this.yPosModeButton, DESCRIPTION_WIDTH, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        root.add(this.yPos, DESCRIPTION_WIDTH + BUTTON_WIDTH + 1, y, TEXT_FIELD_WIDTH, 1);

        y += LINE_HEIGHT;
        root.add(createText(Component.literal("文件名")), 0, y + DESCRIPTION_Y_SHIFT, DESCRIPTION_WIDTH, 1);
        root.add(this.file, DESCRIPTION_WIDTH, y, TEXT_FIELD_WIDTH * 3, 1);

        y += LINE_HEIGHT;
        x = 0;
        this.saveButton.setOnClick(this::save);
        root.add(this.saveButton, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.executeButton.setOnClick(this::execute);
        x += BUTTON_WIDTH + 4;
        root.add(this.executeButton, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        WButton cancel = new WButton(Component.literal("取消"));
        cancel.setOnClick(() -> Minecraft.getInstance().setScreen(null));
        x += BUTTON_WIDTH + 4;
        root.add(cancel, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        root.validate(this);
    }

    @SuppressWarnings("DataFlowIssue")
    private static TooltipText createText(Component component) {
        return new TooltipText(component, ChatFormatting.AQUA.getColor() | 0xFF_000000);
    }

    private void suggest() {
        this.commandSuggestion = this.commandSuggestion.next();
        this.command.setText(this.commandSuggestion.getCommand());
    }

    private void fill() {
        CMLogic.getBox().ifLeft(box -> {
            this.startX.setText(box.minX());
            this.startZ.setText(box.minZ());
            this.endX.setText(box.maxX());
            this.endZ.setText(box.maxZ());
            this.fillButton.setTooltips(Component.literal("填充成功").withStyle(ChatFormatting.GREEN));
        }).ifRight(this.fillButton::setTooltips);
    }

    private void toggleYPosMode() {
        this.yPosModeButton.toggle();
        switch (this.yPosModeButton.getMode()) {
            case FIXED_VALUE -> this.root.setVisible(this.yPos, true);
            case SURFACE -> this.root.setVisible(this.yPos, false);
        }
    }

    private void save() {
        CMInputData.parse(
                this.period.parseInteger(),
                this.command.getText(),
                this.code1.parseInteger(),
                this.code2.parseInteger(),
                this.startX.parseInteger(),
                this.startZ.parseInteger(),
                this.endX.parseInteger(),
                this.endZ.parseInteger(),
                this.yPosModeButton.getMode(),
                this.yPos.parseInteger(),
                this.file.getText()
        ).ifLeft(x -> {
            try {
                CMLogic.save(x);
                this.saveButton.setTooltips(Component.literal("保存成功").withStyle(ChatFormatting.GREEN));
            } catch (RuntimeException e) {
                this.saveButton.setTooltips(
                        Component.literal("保存失败").withStyle(ChatFormatting.RED).append(": "),
                        Component.literal(e.getMessage())
                );
            }
        }).ifRight(x -> this.saveButton.setTooltips(
                Component.literal("保存失败").withStyle(ChatFormatting.RED).append(": "),
                x
        ));
    }

    private void execute() {
        Component component = CommandMacro.run(this.file.getText());
        if (component != null) {
            this.executeButton.setTooltips(
                    Component.literal("执行失败").withStyle(ChatFormatting.RED).append(": "),
                    component
            );
        } else {
            this.executeButton.setTooltips(Component.literal("执行成功").withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public void addPainters() {
        super.addPainters();
        this.rootPanel.setBackgroundPainter(BackgroundPainter.createColorful(1291845632));
    }
}
