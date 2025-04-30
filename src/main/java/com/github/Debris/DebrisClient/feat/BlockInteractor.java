package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.render.RenderQueue;
import com.github.Debris.DebrisClient.render.RendererFactory;
import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BlockInteractor extends ObjectInteractor<BlockPos> {
    public static final BlockInteractor INSTANCE = new BlockInteractor();

    private BlockInteractor() {
    }

    @Override
    protected boolean withinReach(MinecraftClient client, BlockPos object) {
        return InteractionUtil.withinReach(client, object);
    }

    @Override
    protected void interact(MinecraftClient client, BlockPos object) {
        InteractionUtil.interactBlock(client, object);
        if (Predicates.hasMod(ModReference.MagicLibMCApi)) {
            RenderQueue.add(RendererFactory.text(Text.literal("已交互"), object), 100);
        }
    }
}
