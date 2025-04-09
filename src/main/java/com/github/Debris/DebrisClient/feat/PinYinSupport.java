package com.github.Debris.DebrisClient.feat;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.unsafe.rei.ReiPinYinMatcher;
import com.github.Debris.DebrisClient.util.Predicates;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PinYinSupport {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Int2IntMap TONE_MAP = new Int2IntOpenHashMap();
    private static final Int2ObjectMap<String> PINYIN_MAP = new Int2ObjectOpenHashMap<>();

    public static int compareString(String s1, String s2, IntSupplier fallback) {
        int length1 = s1.length();
        int length2 = s2.length();
        int minLength = Math.min(length1, length2);

        int compare;
        for (int i = 0; i < minLength; i++) {
            int codePoint1 = s1.codePointAt(i);
            int codePoint2 = s2.codePointAt(i);
            compare = compareCodePoint(codePoint1, codePoint2, () -> Integer.compare(codePoint1, codePoint2));
            if (compare != 0) return compare;
        }
        compare = Integer.compare(length1, length2);
        return compare != 0 ? compare : fallback.getAsInt();
    }

    private static int compareCodePoint(int codePoint1, int codePoint2, IntSupplier compareByDefault) {
        boolean isHanzi1 = isHanzi(codePoint1);
        boolean isHanzi2 = isHanzi(codePoint2);
        if (isHanzi1 && isHanzi2) {
            return getPinYin(codePoint1).compareTo(getPinYin(codePoint2));
        }
        if (isHanzi1) return 1;// chinese rank behind, so is larger
        if (isHanzi2) return -1;
        return compareByDefault.getAsInt();
    }

    public static boolean matchesFilter(String entryString, String filterText) {
        if (Predicates.hasMod(ModReference.REI)) {
            Optional<Boolean> optionalB = ReiPinYinMatcher.matchesFilter(entryString, filterText);
            if (optionalB.isPresent()) return optionalB.get();
        }
        return entryString.codePoints().anyMatch(codePoint -> {
            if (isHanzi(codePoint)) return getPinYin(codePoint).startsWith(filterText);
            return false;
        });// this is my simple algorithm
    }

    public static String convertToPinYin(String original) {
        StringBuilder stringBuilder = new StringBuilder();
        original.codePoints().forEach(codePoint -> {
            if (isHanzi(codePoint)) {
                stringBuilder.append(getPinYin(codePoint));
            } else {
                stringBuilder.appendCodePoint(codePoint);
            }
        });
        return stringBuilder.toString();
    }

    public static boolean isHanzi(int codePoint) {
        return PINYIN_MAP.containsKey(codePoint);
    }

    public static String getPinYin(int codePoint) {
        return PINYIN_MAP.get(codePoint);
    }

    private static final Path unihanPath = FabricLoader.getInstance().getConfigDir().resolve("roughlyenoughitems/unihan.zip");

    private static boolean available = false;

    public static boolean available() {
        return available;
    }

    private static boolean dataExists() {
        return Files.exists(unihanPath);
    }

    private static boolean tryLoad() {
        String readingsFileName = "Unihan_Readings.txt";
        DataConsumer consumer = (codePoint, fieldKey, data) -> {
            if (fieldKey.equals("kMandarin")) {// only mandarin pinyin used
                data = removeTones(data);
                PINYIN_MAP.put(codePoint, data);
            }
        };

        try (ZipFile zipFile = new ZipFile(unihanPath.toFile())) {
            ZipEntry entry = zipFile.getEntry(readingsFileName);
            if (entry != null) {
                InputStream inputStream = zipFile.getInputStream(entry);
                read(IOUtils.lineIterator(inputStream, StandardCharsets.UTF_8), consumer);
                return true;
            } else {
                LOGGER.error("File {} not in the zip", readingsFileName);
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("failed to read data", e);
            return false;
        }
    }

    private static void read(LineIterator lines, DataConsumer consumer) {
        int i = 0;
        while (lines.hasNext()) {
            i++;
            String line = lines.next();
            if (line.startsWith("#") || line.isEmpty()) continue;
            if (!line.startsWith("U+")) {
                throw new IllegalArgumentException("Invalid line: " + i + ", " + line);
            }
            int firstTab = line.indexOf('\t');
            String code = line.substring(2, firstTab);
            int codePoint = Integer.parseInt(code, 16);
            int secondTab = line.indexOf('\t', firstTab + 1);
            String fieldKey = line.substring(firstTab + 1, secondTab);
            String data = line.substring(secondTab + 1);
            consumer.read(codePoint, fieldKey, data);
        }
    }

    private static String removeTones(String input) {
        StringBuilder output = new StringBuilder();
        for (char c : input.toCharArray()) {
            output.append((char) TONE_MAP.getOrDefault(c, c));
        }
        return output.toString();
    }

    private static void addTone(char c, String s) {
        TONE_MAP.put(c, s.charAt(0));
    }

    static {
        addTone('ā', "a1");
        addTone('á', "a2");
        addTone('ǎ', "a3");
        addTone('à', "a4");
        addTone('ē', "e1");
        addTone('é', "e2");
        addTone('ě', "e3");
        addTone('è', "e4");
        addTone('ī', "i1");
        addTone('í', "i2");
        addTone('ǐ', "i3");
        addTone('ì', "i4");
        addTone('ō', "o1");
        addTone('ó', "o2");
        addTone('ǒ', "o3");
        addTone('ò', "o4");
        addTone('ū', "u1");
        addTone('ú', "u2");
        addTone('ǔ', "u3");
        addTone('ù', "u4");
        addTone('ǖ', "v1");
        addTone('ǘ', "v2");
        addTone('ǚ', "v3");
        addTone('ǜ', "v4");

        if (dataExists()) {
            available = tryLoad();
        }
    }

    @FunctionalInterface
    private interface DataConsumer {
        void read(int codePoint, String fieldKey, String data);
    }
}
