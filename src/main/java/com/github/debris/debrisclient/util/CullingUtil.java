package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import com.github.debris.debrisclient.unsafe.miniHud.MiniHudAccessor;
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
        if (type == BlockEntityType.SIGN && DCCommonConfig.CullSign.getBooleanValue()) return true;
        if (type == BlockEntityType.CHEST && DCCommonConfig.CullChest.getBooleanValue()) return true;

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
        if (DCCommonConfig.MuteExplosion.getBooleanValue() && SoundEvents.ENTITY_GENERIC_EXPLODE.matchesId(id))
            return true;
        if (DCCommonConfig.MuteDispenser.getBooleanValue() && SoundEvents.BLOCK_DISPENSER_FAIL.id().equals(id))
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

    public static boolean shouldCullParticle(ParticleEffect particleEffect) {
        if (particleEffect == ParticleTypes.POOF && DCCommonConfig.CullPoofParticle.getBooleanValue()) return true;

        Identifier id = Registries.PARTICLE_TYPE.getId(particleEffect.getType());
        if (id == null) return false;
        return DCCommonConfig.CullParticleList.getStrings().contains(id.toString());
    }

    @SuppressWarnings("RedundantIfStatement")
    public static boolean shouldCullWthit() {
        if (!DCCommonConfig.WthitMasaCompat.getBooleanValue()) return false;
        if (ModReference.hasMod(ModReference.Litematica) && LitematicaAccessor.isRenderingInfoOverlay()) return true;
        if (ModReference.hasMod(ModReference.MiniHud) && MiniHudAccessor.isPreviewingInventory()) return true;
        return false;
    }
}
