package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import net.minecraft.network.chat.Component;

import java.util.OptionalInt;
import java.util.regex.Pattern;

public class IntegerField extends TextField {
    private static final Pattern PATTERN_NUMBER = Pattern.compile("-?[0-9]*");

    public IntegerField() {
        this(null);
    }

    public IntegerField(Component component) {
        super(component);
        this.setTextPredicate((input) -> input.isEmpty() || PATTERN_NUMBER.matcher(input).matches());
    }

    public OptionalInt parseInteger() {
        try {
            return OptionalInt.of(Integer.parseInt(this.getText()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public IntegerField setText(int i) {
        this.setText(String.valueOf(i));
        return this;
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        super.addTooltip(tooltip);
        if (this.parseInteger().isEmpty()) {
            tooltip.add(Component.literal("不符合整数格式"));
        }
    }
}
