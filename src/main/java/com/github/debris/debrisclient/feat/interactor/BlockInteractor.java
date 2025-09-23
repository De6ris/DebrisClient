package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.localization.InteractionText;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.RendererFactory;
import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BlockInteractor extends ObjectInteractor<BlockPos> {
    public static final BlockInteractor INSTANCE = new BlockInteractor();

    private BlockInteractor() {
    }

    @Override
    protected void informClear() {
        InfoUtils.sendVanillaMessage(InteractionText.STOP_BLOCKS.text());
    }

    @Override
    protected boolean withinReach(MinecraftClient client, BlockPos object) {
        return InteractionUtil.withinReach(client, object);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected InteractResult interact(MinecraftClient client, BlockPos object) {
        boolean isContainer = BlockUtil.isContainer(client.world, object);
        if (isContainer && !Predicates.inGameNoGui(client)) return InteractResult.FAIL;
        InteractionUtil.useBlock(client, object);
        if (ModReference.hasMod(ModReference.MagicLibMCApi)) {
            RenderQueue.add(RendererFactory.text(Text.literal("已交互"), object), 100);
        }
        return isContainer ? InteractResult.WAITING : InteractResult.SUCCESS;
    }
}
