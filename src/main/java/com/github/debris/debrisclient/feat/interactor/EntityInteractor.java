package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.localization.InteractionText;
import com.github.debris.debrisclient.render.RenderQueue;
import com.github.debris.debrisclient.render.RendererFactory;
import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.village.Merchant;

public class EntityInteractor extends ObjectInteractor<Entity> {
    public static final EntityInteractor INSTANCE = new EntityInteractor();

    private EntityInteractor() {
    }

    @Override
    protected void informClear() {
        InfoUtils.sendVanillaMessage(InteractionText.STOP_ENTITIES.text());
    }

    @Override
    protected boolean shouldRemove(Entity object) {
        return object.isRemoved();
    }

    @Override
    protected boolean withinReach(MinecraftClient client, Entity object) {
        return InteractionUtil.withinReach(client, object);
    }

    @Override
    protected InteractResult interact(MinecraftClient client, Entity entity) {
        boolean isContainer = entity instanceof Merchant;
        if (isContainer && !Predicates.inGameNoGui(client)) return InteractResult.FAIL;
        InteractionUtil.useEntity(client, entity);
        if (ModReference.hasMod(ModReference.MagicLibMCApi)) {
            RenderQueue.add(RendererFactory.text(Text.literal("已交互"), entity), 100);
        }
        return isContainer ? InteractResult.WAITING : InteractResult.SUCCESS;
    }
}
