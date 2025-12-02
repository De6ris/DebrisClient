package com.github.debris.debrisclient.util;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import org.slf4j.Logger;

import java.util.Collection;

public class StringUtil {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static String translate(String key) {
        return StringUtils.translate(key);
    }

    public static String translateFallback(String key, String fallback) {
        String format = StringUtils.translate(key);
        if (format.equals(key)) {
            LOGGER.info("missing translation key {}", key);
            return fallback;
        }
        return format;
    }

    public static String translateItem(Item item) {
        return StringUtils.translate(item.getDescriptionId());
    }

    public static String translateItemCollection(Collection<Item> items) {
        return items.stream().map(StringUtil::translateItem).toList().toString();
    }

    public static String translateBiome(Biome biome) {
        Identifier id = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
        String translationKey = "biome." + id.toString().replace(":", ".");
        String biomeName = StringUtils.translate(translationKey);
        if (biomeName.equals(translationKey)) {
            biomeName = StringUtils.prettifyRawTranslationPath(id.getPath());
        }
        return biomeName;
    }

    public static <T extends Enum<T>> String convertEnumClassName(Class<T> clazz) {
        String simpleName = clazz.getSimpleName();
        return CaseFormat.UPPER_CAMEL
                .converterTo(CaseFormat.LOWER_UNDERSCORE)
                .convert(simpleName);
    }
}
