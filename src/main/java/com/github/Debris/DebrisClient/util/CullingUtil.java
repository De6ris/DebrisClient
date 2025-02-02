package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class CullingUtil {
    public static boolean shouldCullEntity(EntityType<?> type) {
        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (DCCommonConfig.CancelFrameRendering.getBooleanValue() && isFrame) return true;

        if (DCCommonConfig.CancelItemEntityRendering.getBooleanValue() && type == EntityType.ITEM) return true;

        if (DCCommonConfig.CancelExperienceOrbRendering.getBooleanValue() && type == EntityType.EXPERIENCE_ORB)
            return true;

        return false;
    }

    public static boolean shouldMuteSound(SoundInstance soundInstance) {
        Identifier id = soundInstance.getId();
        if (SoundEvents.ENTITY_GENERIC_EXPLODE.matchesId(id)) {
            return DCCommonConfig.MuteExplosion.getBooleanValue();
        }
        if (SoundEvents.BLOCK_DISPENSER_FAIL.id().equals(id)) {
            return DCCommonConfig.MuteDispenser.getBooleanValue();
        }
        String path = id.getPath();
        if (path.startsWith("entity.wither")) {
            return DCCommonConfig.MuteWither.getBooleanValue();
        }
        if (path.startsWith("entity.enderman")) {
            return DCCommonConfig.MuteEnderman.getBooleanValue();
        }
        if (path.startsWith("entity.minecart")) {
            return DCCommonConfig.MuteMinecart.getBooleanValue();
        }
        if (path.startsWith("entity.lightning_bolt")){
            return DCCommonConfig.MuteThunder.getBooleanValue();
        }
        return false;
    }

    public static boolean shouldCullParticle(ParticleEffect particleEffect) {
        if (particleEffect == ParticleTypes.POOF) {
            return DCCommonConfig.CullPoofParticle.getBooleanValue();
        }
        return false;
    }
}
