package com.github.debris.debrisclient.render;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.magicLib.MagicLibAccessor;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class RendererFactory {
    @Condition(ModReference.MagicLibMCApi)
    public static Renderer text(Text text, BlockPos pos) {
        return MagicLibAccessor.createTextRenderer(text, pos);
    }

    @Condition(ModReference.MagicLibMCApi)
    public static Renderer text(Text text, Entity entity) {
        return MagicLibAccessor.createTextRenderer(text, entity);
    }
}
