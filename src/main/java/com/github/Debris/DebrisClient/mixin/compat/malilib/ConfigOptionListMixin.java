package com.github.Debris.DebrisClient.mixin.compat.malilib;

import com.github.Debris.DebrisClient.config.api.IIConfigOptionList;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfigOptionList.class, remap = false)
public class ConfigOptionListMixin implements IIConfigOptionList {
    @Unique
    private ImmutableList<IConfigOptionListEntry> entries;

    @Inject(method = "<init>(Ljava/lang/String;Lfi/dy/masa/malilib/config/IConfigOptionListEntry;Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onInit(String name, IConfigOptionListEntry defaultValue, String comment, String prettyName, CallbackInfo ci) {
        ImmutableList.Builder<IConfigOptionListEntry> builder = ImmutableList.builder();
        builder.add(defaultValue);
        IConfigOptionListEntry next = defaultValue.cycle(true);
        while (next != defaultValue) {
            builder.add(next);
            next = next.cycle(true);
        }
        this.entries = builder.build();
    }

    @Override
    public ImmutableList<IConfigOptionListEntry> dc$getEntries() {
        return this.entries;
    }
}
