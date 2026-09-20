package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.feat.task.FutureTaskQueue;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.unsafe.CMScreenAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CMApi {
    @Nullable
    public static Component openGui(Minecraft client) {
        if (ModReference.hasMod(ModReference.LibGui)) {
            FutureTaskQueue.scheduleNextTick(client_ -> client_.setScreenAndShow(CMScreenAccess.getCMScreen()));
            return null;
        } else {
            return GeneralText.FEATURE_REQUIRES_MOD.translate(ModReference.LibGui);
        }
    }
}
