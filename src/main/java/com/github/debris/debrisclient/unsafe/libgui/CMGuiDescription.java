package com.github.debris.debrisclient.unsafe.libgui;

import com.github.debris.debrisclient.feat.commandmacro.*;
import com.github.debris.debrisclient.util.ChatUtil;
import com.mojang.datafixers.util.Either;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CMGuiDescription extends LightweightGuiDescription {
    private static final int GRID_SIZE = 5;

    private static final int LINE_HEIGHT = 5;

    private static final int DESCRIPTION_Y_SHIFT = 1;
    private static final int DESCRIPTION_WIDTH = 14;

    private static final int TEXT_FIELD_WIDTH = 8;

    private static final int BUTTON_WIDTH = 8;
    private static final int BUTTON_HEIGHT = 4;

    private static final int GAP = 1;

    private final SuppressivePanel root = new SuppressivePanel(GRID_SIZE);

    private final IntegerField period = new IntegerField().setText(CMLogic.DEFAULT_PERIOD);

    private final TextField command = new TextField(Component.literal(CMLogic.DEFAULT_COMMAND)) {
        {
            this.setMaxLength(256);
            this.setText(CMLogic.DEFAULT_COMMAND);
        }
    };
    private BuiltInCM commandSuggestion = BuiltInCM.SPAWN;

    private final TooltipButton fillCodeButton = new TooltipButton(Component.literal("填充"))
            .setTooltips(Component.literal("根据litematica选区信息"));
    private final IntegerField code1 = new IntegerField().setText(0);
    private final IntegerField code2 = new IntegerField().setText(9);

    private final TooltipButton fillPosButton = new TooltipButton(Component.literal("填充"))
            .setTooltips(Component.literal("根据litematica选区信息"));
    private final IntegerField startX = new IntegerField(Component.literal("起点X"));
    private final IntegerField startZ = new IntegerField(Component.literal("起点Z"));
    private final IntegerField endX = new IntegerField(Component.literal("终点X"));
    private final IntegerField endZ = new IntegerField(Component.literal("终点Z"));

    private final YPosModeButton yPosModeButton = new YPosModeButton();
    private final IntegerField yPos = new IntegerField().setText(100);

    private final WTextField file = new WTextField(Component.literal(CMLogic.DEFAULT_FILE)) {
        {
            this.setMaxLength(128);
            this.setText(CMLogic.DEFAULT_FILE);
        }
    };

    private final TooltipButton saveButton = new TooltipButton(Component.literal("保存"));
    private final TooltipButton executeButton = new TooltipButton(Component.literal("执行"));


    public CMGuiDescription() {
        this.setupRoot(this.root);
        this.root.setVisible(this.yPos, false);
        this.command.onFocusLost();// to trigger callback
        this.setRootPanel(this.root);
    }

    private void setupRoot(SuppressivePanel root) {
        root.setSize(324, 192);
        root.setInsets(Insets.ROOT_PANEL);

        PosHelper helper = new PosHelper(root);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("时间间隔")).setTooltips(
                        Component.literal("单位为tick")
                ),
                DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH
        );
        helper.putWidget(this.period, TEXT_FIELD_WIDTH, 1);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("指令内容")).setTooltips(
                        Component.literal("以下占位符允许你动态填充内容:"),
                        Component.literal("${code}: bot编号"),
                        Component.literal("${pos}: bot坐标")
                ),
                DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH);
        this.command.setFinishCallback(this::onCommandFinish);
        helper.putWidget(this.command, TEXT_FIELD_WIDTH * 5, 1, GAP);
        TooltipButton suggestButton = new TooltipButton(Component.literal("预设"));
        suggestButton.setOnClick(this::suggest);
        helper.putWidget(suggestButton, BUTTON_WIDTH, BUTTON_HEIGHT);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("bot编号")).setTooltips(
                        Component.literal("指令中出现坐标时, 此选项无效")
                ),
                DESCRIPTION_Y_SHIFT,
                DESCRIPTION_WIDTH);
        this.fillCodeButton.setOnClick(this::fillCode);
        helper.putWidget(this.fillCodeButton, BUTTON_WIDTH, BUTTON_HEIGHT, GAP);
        helper.putWidget(this.code1, TEXT_FIELD_WIDTH, 1, GAP);
        helper.putWidget(this.code2, TEXT_FIELD_WIDTH, 1);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("botXZ坐标范围")), DESCRIPTION_Y_SHIFT, DESCRIPTION_WIDTH);
        this.fillPosButton.setOnClick(this::fillPos);
        helper.putWidget(this.fillPosButton, BUTTON_WIDTH, BUTTON_HEIGHT, GAP);
        helper.putWidget(this.startX, TEXT_FIELD_WIDTH, 1, GAP);
        helper.putWidget(this.startZ, TEXT_FIELD_WIDTH, 1, GAP);
        helper.putWidget(this.endX, TEXT_FIELD_WIDTH, 1, GAP);
        helper.putWidget(this.endZ, TEXT_FIELD_WIDTH, 1, GAP);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("botY坐标")), DESCRIPTION_Y_SHIFT, DESCRIPTION_WIDTH);
        this.yPosModeButton.setOnClick(this::toggleYPosMode);
        helper.putWidget(this.yPosModeButton, BUTTON_WIDTH, BUTTON_HEIGHT, GAP);
        helper.putWidget(this.yPos, TEXT_FIELD_WIDTH, 1);

        helper.newline(LINE_HEIGHT);
        helper.putText(createText(Component.literal("文件名")), DESCRIPTION_Y_SHIFT, DESCRIPTION_WIDTH);
        helper.putWidget(this.file, TEXT_FIELD_WIDTH * 3, 1);

        helper.newline(LINE_HEIGHT);
        WButton helpButton = new WButton(Component.literal("帮助"));
        helpButton.setOnClick(() -> {
            Minecraft client = Minecraft.getInstance();
            ChatUtil.sendChat(client, "/dccommand_macro help");
            client.setScreen(new ChatScreen("", false));
        });
        helper.putWidget(helpButton, BUTTON_WIDTH, BUTTON_HEIGHT, 4 * GAP);
        this.saveButton.setOnClick(this::save);
        helper.putWidget(this.saveButton, BUTTON_WIDTH, BUTTON_HEIGHT, 4 * GAP);
        this.executeButton.setOnClick(this::execute);
        helper.putWidget(this.executeButton, BUTTON_WIDTH, BUTTON_HEIGHT, 4 * GAP);
        WButton cancelButton = new WButton(Component.literal("取消"));
        cancelButton.setOnClick(() -> Minecraft.getInstance().setScreen(null));
        helper.putWidget(cancelButton, BUTTON_WIDTH, BUTTON_HEIGHT, 4 * GAP);

        root.validate(this);
    }

    @SuppressWarnings("DataFlowIssue")
    private static TooltipText createText(Component component) {
        return new TooltipText(component, ChatFormatting.AQUA.getColor() | 0xFF_000000);
    }

    private void onCommandFinish(String command) {
        CMContext.Type type = CMLogic.getType(command);
        boolean requiresPos = type == CMContext.Type.SPAWN;
        boolean requireCode = !requiresPos;
        this.setGroupEnabled(this.getCodeGroup(), requireCode);
        this.setGroupEnabled(this.getPosGroup(), requiresPos);
    }

    private List<WWidget> getCodeGroup() {
        return List.of(this.fillCodeButton, this.code1, this.code2);
    }

    private List<WWidget> getPosGroup() {
        return List.of(this.fillPosButton, this.startX, this.startZ, this.endX, this.endZ, this.yPosModeButton, this.yPos);
    }

    private void setGroupEnabled(List<WWidget> group, boolean enabled) {
        group.forEach(x -> {
            if (x instanceof WButton button) button.setEnabled(enabled);
            if (x instanceof WTextField textField) textField.setEditable(enabled);
        });
    }

    private void suggest() {
        this.commandSuggestion = this.commandSuggestion.next();
        this.command.setText(this.commandSuggestion.getCommand());
        this.command.onFocusLost();// to trigger callback
    }

    private void fillCode() {
        CMLogic.getBox().ifLeft(box -> {
            this.code2.setText(box.getXSpan() * box.getZSpan() - 1);
            this.fillCodeButton.setTooltips(Component.literal("填充成功").withStyle(ChatFormatting.GREEN));
        }).ifRight(this.fillCodeButton::setTooltips);
    }

    private void fillPos() {
        CMLogic.getBox().ifLeft(box -> {
            this.startX.setText(box.minX());
            this.startZ.setText(box.minZ());
            this.endX.setText(box.maxX());
            this.endZ.setText(box.maxZ());
            this.fillPosButton.setTooltips(Component.literal("填充成功").withStyle(ChatFormatting.GREEN));
        }).ifRight(this.fillPosButton::setTooltips);
    }

    private void toggleYPosMode() {
        this.yPosModeButton.toggle();
        switch (this.yPosModeButton.getMode()) {
            case FIXED_VALUE -> this.root.setVisible(this.yPos, true);
            case SURFACE -> this.root.setVisible(this.yPos, false);
        }
    }

    private void save() {
        this.parseInputData().ifLeft(x -> {
            if (CMLogic.save(x)) {
                this.saveButton.setTooltips(Component.literal("保存成功").withStyle(ChatFormatting.GREEN));
            } else {
                this.saveButton.setTooltips(Component.literal("保存失败").withStyle(ChatFormatting.RED).append(": "), Component.literal("无法创建文件"));
            }
        }).ifRight(x -> this.saveButton.setTooltips(Component.literal("保存失败").withStyle(ChatFormatting.RED).append(": "), x));
    }

    private Either<CMInputData, Component> parseInputData() {
        return CMInputData.parse(this.period.parseInteger(),
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
        );
    }

    private void execute() {
        this.parseInputData().ifLeft(x -> {
            CommandMacro macro = CMLogic.generateMacro(x);
            macro.run();
            this.executeButton.setTooltips(Component.literal("执行成功").withStyle(ChatFormatting.GREEN));
        }).ifRight(component -> this.executeButton.setTooltips(Component.literal("执行失败").withStyle(ChatFormatting.RED).append(": "), component));
    }

    @Override
    public void addPainters() {
        this.rootPanel.setBackgroundPainter(BackgroundPainter.createColorful(1291845632));
    }
}
