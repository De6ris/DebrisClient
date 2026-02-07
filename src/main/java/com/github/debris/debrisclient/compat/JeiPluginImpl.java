package com.github.debris.debrisclient.compat;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.unsafe.JeiAccess;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class JeiPluginImpl implements IModPlugin {
    @Override
    public @NonNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(DebrisClient.MOD_ID, "core");
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime jeiRuntime) {
        JeiAccess.jeiRuntime = jeiRuntime;
    }
}
