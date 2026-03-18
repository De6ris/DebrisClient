package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.feat.task.FutureTaskQueue;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.unsafe.CMGuiAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CMGenerator {
    @Nullable
    public static Component openGui(Minecraft client) {
        if (ModReference.hasMod(ModReference.LibGui)) {
            FutureTaskQueue.scheduleNextTick(client_ -> client_.setScreen(CMGuiAccess.getScreen()));
            return null;
        } else {
            return GeneralText.FEATURE_REQUIRES_MOD.translate(ModReference.LibGui);
        }
    }
}
