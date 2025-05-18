package com.github.debris.debrisclient.util;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.item.Item;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Optional;

public class StringUtil {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isModLoadedWithNewEnoughVersion(String modId, String leastVersion) {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isEmpty()) return false;
        Version version = optional.get().getMetadata().getVersion();
        try {
            Version parse = Version.parse(leastVersion);
            if (version.compareTo(parse) >= 0) return true;
        } catch (VersionParsingException e) {
            return false;
        }
        return false;
    }

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
        return StringUtils.translate(item.getTranslationKey());
    }

    public static String translateItemCollection(Collection<Item> items) {
        return items.stream().map(StringUtil::translateItem).toList().toString();
    }

    public static <T extends Enum<T>> String convertEnumClassName(Class<T> clazz) {
        String simpleName = clazz.getSimpleName();
        return CaseFormat.UPPER_CAMEL
                .converterTo(CaseFormat.LOWER_UNDERSCORE)
                .convert(simpleName);
    }
}
