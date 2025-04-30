package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.render.RenderQueue;
import com.github.Debris.DebrisClient.render.RendererFactory;
import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class EntityInteractor extends ObjectInteractor<Entity> {
    public static final EntityInteractor INSTANCE = new EntityInteractor();

    private EntityInteractor() {
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
    protected void interact(MinecraftClient client, Entity entity) {
        InteractionUtil.useEntity(client, entity);
        if (Predicates.hasMod(ModReference.MagicLibMCApi)) {
            RenderQueue.add(RendererFactory.text(Text.literal("已交互"), entity), 100);
        }
    }
}
