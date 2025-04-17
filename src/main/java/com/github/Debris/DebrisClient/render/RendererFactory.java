package com.github.Debris.DebrisClient.render;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.unsafe.magicLib.MagicLibAccessor;
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
