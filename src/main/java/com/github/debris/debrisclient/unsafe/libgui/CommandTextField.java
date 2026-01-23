package com.github.debris.debrisclient.unsafe.libgui;

import com.github.debris.debrisclient.feat.commandmacro.CMLogic;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CommandTextField extends WTextField {
    @Nullable
    private Consumer<String> callback;

    public CommandTextField() {
        super(Component.literal(CMLogic.DEFAULT_COMMAND));
        this.setMaxLength(256);
        this.setText(CMLogic.DEFAULT_COMMAND);
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
