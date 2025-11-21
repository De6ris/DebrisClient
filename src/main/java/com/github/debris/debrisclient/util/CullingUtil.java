package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.unsafe.MiniHudAccess;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CullingUtil {
    public static boolean shouldCullBlockEntity(BlockEntityType<?> type) {
        if (type == BlockEntityType.SIGN && DCCommonConfig.CullSign.getBooleanValue()) return true;
        if (type == BlockEntityType.CHEST && DCCommonConfig.CullChest.getBooleanValue()) return true;

        ResourceLocation id = BlockEntityType.getKey(type);
        if (id == null) return false;
        return DCCommonConfig.CullBlockEntityList.getStrings().contains(id.toString());
    }

    public static boolean shouldCullEntity(EntityType<?> type) {
        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (DCCommonConfig.CullItemFrame.getBooleanValue() && isFrame) return true;
        if (DCCommonConfig.CullItemEntity.getBooleanValue() && type == EntityType.ITEM) return true;
        if (DCCommonConfig.CullExperienceOrb.getBooleanValue() && type == EntityType.EXPERIENCE_ORB) return true;

        return DCCommonConfig.CullEntityList.getStrings().contains(EntityType.getKey(type).toString());
    }

    public static boolean shouldMuteSound(SoundInstance soundInstance) {
        ResourceLocation id = soundInstance.getLocation();
        if (DCCommonConfig.MuteExplosion.getBooleanValue() && SoundEvents.GENERIC_EXPLODE.is(id))
            return true;
        if (DCCommonConfig.MuteDispenser.getBooleanValue() && SoundEvents.DISPENSER_FAIL.location().equals(id))
            return true;

        String path = id.getPath();

        if (DCCommonConfig.MuteWither.getBooleanValue() && path.startsWith("entity.wither")) return true;
        if (DCCommonConfig.MuteEnderman.getBooleanValue() && path.startsWith("entity.enderman")) return true;
        if (DCCommonConfig.MuteZombifiedPiglin.getBooleanValue() && path.startsWith("entity.zombified_piglin")) return true;
        if (DCCommonConfig.MuteMinecart.getBooleanValue() && path.startsWith("entity.minecart")) return true;
        if (DCCommonConfig.MuteThunder.getBooleanValue() && path.startsWith("entity.lightning_bolt")) return true;
        if (DCCommonConfig.MuteGuardian.getBooleanValue() && path.startsWith("entity.guardian")) return true;
        if (DCCommonConfig.MuteAnvil.getBooleanValue() && path.startsWith("block.anvil")) return true;
        if (DCCommonConfig.MuteDoor.getBooleanValue() && path.contains("block.") && path.contains("door")) return true;

        return DCCommonConfig.MuteSoundList.getStrings().contains(id.toString());
    }

    public static boolean shouldCullParticle(ParticleOptions particleEffect) {
        if (particleEffect == ParticleTypes.POOF && DCCommonConfig.CullPoofParticle.getBooleanValue()) return true;

        ResourceLocation id = BuiltInRegistries.PARTICLE_TYPE.getKey(particleEffect.getType());
        if (id == null) return false;
        return DCCommonConfig.CullParticleList.getStrings().contains(id.toString());
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean shouldCullWthit() {
        if (!DCCommonConfig.WthitMasaCompat.getBooleanValue()) return false;
        if (ModReference.hasMod(ModReference.Litematica) && LitematicaAccessor.isRenderingInfoOverlay()) return true;
        if (ModReference.hasMod(ModReference.MiniHud) && MiniHudAccess.isPreviewingInventory()) return true;
        return false;
    }
}
