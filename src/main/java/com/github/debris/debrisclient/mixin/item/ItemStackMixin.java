package com.github.debris.debrisclient.mixin.item;

import com.github.debris.debrisclient.feat.ExtraTooltip;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    // This Goes before the Item durability, item id, and component count.
    @Inject(method = "appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendComponentTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 21,
                    shift = At.Shift.AFTER))
    private void onGetTooltipComponentsLast(Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                            PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        ExtraTooltip.onTooltip((ItemStack) (Object) this, context, displayComponent, player, type, textConsumer);
    }
}
