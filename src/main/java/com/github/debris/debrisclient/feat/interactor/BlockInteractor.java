package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.RendererFactory;
import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.Predicates;
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
    protected boolean interact(MinecraftClient client, BlockPos object) {
        InteractionUtil.useBlock(client, object);
        if (Predicates.hasMod(ModReference.MagicLibMCApi)) {
            RenderQueue.add(RendererFactory.text(Text.literal("已交互"), object), 100);
        }
        return true;
    }
}
