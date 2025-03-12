package com.github.Debris.DebrisClient.mixin.misc;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.PinYinSupport;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.SuffixArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(SearchProvider.class)
public interface SearchProviderMixin {
    @Inject(method = "plainText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/search/SuffixArray;build()V"))
    private static <T> void addPinYin(List<T> list, Function<T, Stream<String>> function, CallbackInfoReturnable<SearchProvider<T>> cir, @Local SuffixArray<T> suffixArray) {
        if (DCCommonConfig.PinYinSearch.getBooleanValue() && PinYinSupport.available()) {
            for (T object : list) {
                Stream<String> tooltips = function.apply(object);
                tooltips.map(PinYinSupport::convertToPinYin)
                        .forEach(pinYin -> suffixArray.add(object, pinYin));
            }
        }
    }
}
