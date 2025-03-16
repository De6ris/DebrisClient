package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaAccessor;
import com.github.Debris.DebrisClient.unsafe.miniHud.MiniHudConfigAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CullingUtil {
    public static boolean shouldCullEntity(EntityType<?> type) {
        if (DCCommonConfig.CullEntityList.getStrings().stream().anyMatch(x -> type.getTranslationKey().contains(x))) {
            return true;
        }

        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (DCCommonConfig.CullItemFrame.getBooleanValue() && isFrame) return true;

        if (DCCommonConfig.CullItemEntity.getBooleanValue() && type == EntityType.ITEM) return true;

        if (DCCommonConfig.CullExperienceOrb.getBooleanValue() && type == EntityType.EXPERIENCE_ORB)
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

        if (DCCommonConfig.MuteSoundList.getStrings().stream().anyMatch(path::contains)) return true;

        if (path.startsWith("entity.wither")) {
            return DCCommonConfig.MuteWither.getBooleanValue();
        }
        if (path.startsWith("entity.enderman")) {
            return DCCommonConfig.MuteEnderman.getBooleanValue();
        }
        if (path.startsWith("entity.minecart")) {
            return DCCommonConfig.MuteMinecart.getBooleanValue();
        }
        if (path.startsWith("entity.lightning_bolt")) {
            return DCCommonConfig.MuteThunder.getBooleanValue();
        }
        if (path.startsWith("entity.guardian")) {
            return DCCommonConfig.MuteGuardian.getBooleanValue();
        }
        if (path.startsWith("block.anvil")) {
            return DCCommonConfig.MuteAnvil.getBooleanValue();
        }
        return false;
    }

    public static boolean shouldCullParticle(ParticleEffect particleEffect) {
        RegistryEntry<ParticleType<?>> entry = Registries.PARTICLE_TYPE.getEntry(particleEffect.getType());
        Optional<RegistryKey<ParticleType<?>>> optional = entry.getKey();
        if (optional.isPresent()) {
            String path = optional.get().getValue().getPath();
            if (DCCommonConfig.CullParticleList.getStrings().stream().anyMatch(path::contains)) {
                return true;
            }
        }

        if (particleEffect == ParticleTypes.POOF) {
            return DCCommonConfig.CullPoofParticle.getBooleanValue();
        }
        return false;
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
