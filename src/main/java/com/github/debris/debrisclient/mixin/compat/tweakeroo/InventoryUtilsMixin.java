package com.github.debris.debrisclient.mixin.compat.tweakeroo;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.fix.ToolSwitchFix;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = InventoryUtils.class)
public abstract class InventoryUtilsMixin {
    @Shadow
    private static int getMaterialWeight(ItemStack stack) {
        return 0;
    }

    @ModifyExpressionValue(method = "isBetterTool", at = @At(value = "INVOKE", target = "Ljava/util/Map;ofEntries([Ljava/util/Map$Entry;)Ljava/util/Map;"))
    private static Map<String, Supplier<Boolean>> fix(
            Map<String, Supplier<Boolean>> original,
            @Local(argsOnly = true, ordinal = 0) ItemStack testedStack,
            @Local(argsOnly = true, ordinal = 1) ItemStack previousTool,
            @Local(argsOnly = true) BlockState state
    ) {
        if (DCCommonConfig.ToolSwitchFix.getBooleanValue()) {
            Map<String, Supplier<Boolean>> newMap = new HashMap<>(original);
            newMap.put("betterMaterial", () -> ToolSwitchFix.compareMaterial(testedStack, previousTool, state, InventoryUtilsMixin::getMaterialWeight));
            return newMap;
        }
        return original;
    }
}
