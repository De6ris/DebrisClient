package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaAccessor;
import com.github.Debris.DebrisClient.unsafe.miniHud.MiniHudConfigAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class CullingUtil {
    public static boolean shouldCullBlockEntity(BlockEntityType<?> type) {
        if (type == BlockEntityType.SIGN && DCCommonConfig.CullSignRendering.getBooleanValue()) return true;

        Identifier id = BlockEntityType.getId(type);
        if (id == null) return false;
        return DCCommonConfig.CullBlockEntityList.getStrings().contains(id.toString());
    }

    public static boolean shouldCullEntity(EntityType<?> type) {
        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (DCCommonConfig.CullItemFrame.getBooleanValue() && isFrame) return true;
        if (DCCommonConfig.CullItemEntity.getBooleanValue() && type == EntityType.ITEM) return true;
        if (DCCommonConfig.CullExperienceOrb.getBooleanValue() && type == EntityType.EXPERIENCE_ORB) return true;

        return DCCommonConfig.CullEntityList.getStrings().contains(EntityType.getId(type).toString());
    }

    public static boolean shouldMuteSound(SoundInstance soundInstance) {
        Identifier id = soundInstance.getId();
        if (SoundEvents.ENTITY_GENERIC_EXPLODE.matchesId(id) && DCCommonConfig.MuteExplosion.getBooleanValue())
            return true;
        if (SoundEvents.BLOCK_DISPENSER_FAIL.id().equals(id) && DCCommonConfig.MuteDispenser.getBooleanValue())
            return true;

        String path = id.getPath();

        if (path.startsWith("entity.wither") && DCCommonConfig.MuteWither.getBooleanValue()) return true;
        if (path.startsWith("entity.enderman") && DCCommonConfig.MuteEnderman.getBooleanValue()) return true;
        if (path.startsWith("entity.minecart") && DCCommonConfig.MuteMinecart.getBooleanValue()) return true;
        if (path.startsWith("entity.lightning_bolt") && DCCommonConfig.MuteThunder.getBooleanValue()) return true;
        if (path.startsWith("entity.guardian") && DCCommonConfig.MuteGuardian.getBooleanValue()) return true;
        if (path.startsWith("block.anvil") && DCCommonConfig.MuteAnvil.getBooleanValue()) return true;

        return DCCommonConfig.MuteSoundList.getStrings().contains(id.toString());
    }

    public static boolean shouldCullParticle(ParticleEffect particleEffect) {
        if (particleEffect == ParticleTypes.POOF && DCCommonConfig.CullPoofParticle.getBooleanValue()) return true;

        Identifier id = Registries.PARTICLE_TYPE.getId(particleEffect.getType());
        if (id == null) return false;
        return DCCommonConfig.CullParticleList.getStrings().contains(id.toString());
    }

    public static boolean shouldCullWthit() {
        if (!DCCommonConfig.WthitMasaCompat.getBooleanValue()) return false;
        if (FabricLoader.getInstance().isModLoaded(ModReference.Litematica) && LitematicaAccessor.isRenderingInfoOverlay()) {
            return true;
        }
        if (FabricLoader.getInstance().isModLoaded(ModReference.MiniHud) && MiniHudConfigAccessor.isPreviewingInventory()) {
            return true;
        }
        return false;
    }
}
