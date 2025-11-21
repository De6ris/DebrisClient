package com.github.debris.debrisclient.mixin.client.search;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.PinYinSupport;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.client.searchtree.SuffixArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(SearchTree.class)
public interface SearchProviderMixin {
    @Inject(method = "plainText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/searchtree/SuffixArray;generate()V"))
    private static <T> void addPinYin(List<T> list, Function<T, Stream<String>> function, CallbackInfoReturnable<SearchTree<T>> cir, @Local SuffixArray<T> suffixArray) {
        if (DCCommonConfig.PinYinSearch.getBooleanValue() && PinYinSupport.available()) {
            for (T object : list) {
                Stream<String> tooltips = function.apply(object);
                tooltips.map(PinYinSupport::convertToPinYin)
                        .forEach(pinYin -> suffixArray.add(object, pinYin));
            }
        }
    }
}
