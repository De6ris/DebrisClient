package com.github.Debris.DebrisClient.mixin.compat.tweakeroo;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import fi.dy.masa.tweakeroo.util.InventoryUtils;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(require = @Condition(ModReference.Tweakeroo))
@Mixin(value = InventoryUtils.class, remap = false)
public class InventoryUtilsMixin {
    @Redirect(method = "isBetterTool", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0, remap = true), remap = false)
    private static boolean fix(ItemStack instance) {
        if (DCCommonConfig.ToolSwitchFix.getBooleanValue()) return false;
        return instance.isEmpty();
    }
}
