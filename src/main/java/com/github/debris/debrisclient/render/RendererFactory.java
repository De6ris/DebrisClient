package com.github.debris.debrisclient.render;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.MagicLibAccess;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class RendererFactory {
    @Condition(ModReference.MagicLibMCApi)
    public static Renderer text(Component text, BlockPos pos) {
        return MagicLibAccess.createTextRenderer(text, pos);
    }

    @Condition(ModReference.MagicLibMCApi)
    public static Renderer text(Component text, Entity entity) {
        return MagicLibAccess.createTextRenderer(text, entity);
    }
}
