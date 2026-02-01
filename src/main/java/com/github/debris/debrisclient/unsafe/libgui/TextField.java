package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class TextField extends WTextField {
    @Nullable
    private Consumer<String> callback;

    public TextField(Component suggestion) {
        super(suggestion);
    }

    @Override
    public InputResult onClick(MouseButtonEvent click, boolean doubled) {
        if (!this.isEditable()) return InputResult.IGNORED;
        return super.onClick(click, doubled);
    }

    public void setFinishCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    @Override
    public void onFocusLost() {
        if (this.callback != null) {
            this.callback.accept(this.getText());
        }
    }
}
