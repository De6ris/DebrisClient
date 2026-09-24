package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.gui.InventoryConfigScreen;
import com.github.debris.debrisclient.inventory.sort.SortCategory;
import com.github.debris.debrisclient.unsafe.itemScroller.MassCraftingImpl;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.List;

import static com.github.debris.debrisclient.config.ConfigFactory.*;

public class InventoryConfig extends ConfigHandlerImpl {
    private static final InventoryConfig INSTANCE;
    public static final Identifier ID = Identifier.fromNamespaceAndPath(DebrisClient.MOD_ID, "inventory");
    public static final ModInfo MOD_INFO = new ModInfo(ID.toString(), DebrisClient.MOD_NAME + " Inventory", () -> InventoryConfigScreen.getInstance(null));


    // value
    public static final ConfigBoolean SwitchPreset = ofBoolean("切换预设", false, "切换该选项的值即可启用或禁用全部物品栏功能");
    public static final ConfigBoolean SortingContainersLast = ofBoolean("整理时容器置于末端", true, "潜影盒, 收纳袋");
    public static final ConfigBoolean CachedSorting = ofBoolean("整理时使用缓存算法", true, "相比直接操作, 可减少发包");
    public static final ConfigEnum<SortCategory> ItemSortingOrder = ofEnum("物品整理顺序", SortCategory.CREATIVE_INVENTORY, "1.翻译键顺序\n2.按创造模式物品栏顺序\n3.按翻译后名称顺序\n4.按拼音顺序(需要Rei)");
    public static final ConfigEnum<MassCraftingImpl> MassCraftingMode = ofEnum("喷射合成模式", MassCraftingImpl.RECIPE_BOOK, "配方书依赖服务器,较慢但不出错\n手动依赖客户端,可能与服务器不同步导致合成错误");


    // key settings
    private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);
    private static final KeybindSettings ANY = KeybindSettings.create(KeybindSettings.Context.ANY, KeyAction.PRESS, false, true, false, true);


    // list
    public static final ConfigStringList AutoThrowWhiteList = ofStringList("自动丢弃白名单");


    // hotkey
    public static final ConfigHotkey SortInventory = ofHotkey("整理物品栏", "", KeybindSettings.GUI, "按区域进行\n兼容carpet假人不会乱点按钮\n兼容创造模式物品栏");
    public static final ConfigHotkey ModifierSpreadItem = ofHotkey("分散物品:修饰键", "", GUI_RELAXED_CANCEL, "按住时点击会尝试将手中物品均分到点击区域全部槽位");
    public static final ConfigHotkey ModifierMoveSame = ofHotkey("移动相同:修饰键", "", GUI_RELAXED_CANCEL, "按住时左键会移动当前区域相同物品");
    public static final ConfigHotkey ModifierMoveStack = ofHotkey("移动一组:修饰键", "", GUI_RELAXED_CANCEL, "按住时左键会移动当前物品");
    public static final ConfigHotkey ModifierMoveAll = ofHotkey("移动全部:修饰键", "", GUI_RELAXED_CANCEL, "按住时左键会移动当前区域全部\n兼容carpet假人不会乱点按钮");
    public static final ConfigHotkey ModifierClearBundle = ofHotkey("清空收纳袋:修饰键", "", GUI_RELAXED_CANCEL, "");
    public static final ConfigHotkey MyMassCrafting = ofHotkey("我的喷射合成", "", GUI_NO_ORDER, "作为ItemScroller的替代品\n虽然仍然需要安装它才能用(以便读取配方)\n而且需要较高版本");
    public static final ConfigHotkey StoneCutterRecipeView = ofHotkey("展示切石机配方", "A", GUI_RELAXED);
    public static final ConfigHotkey StoreStoneCutterRecipe = ofHotkey("储存切石机配方", "BUTTON_3", GUI_RELAXED_CANCEL);
    public static final ConfigHotkey CutStone = ofHotkey("切石", "LEFT_CONTROL,C", GUI_NO_ORDER);
    public static final ConfigHotkey CutStoneAndThrow = ofHotkey("切石并丢出", "LEFT_CONTROL,LEFT_ALT,C", GUI_NO_ORDER);
    public static final ConfigHotkey ThrowSection = ofHotkey("清空区域", "", KeybindSettings.GUI, "全部丢出");
    public static final ConfigHotkey ThrowSame = ofHotkey("丢出相同", "", KeybindSettings.GUI);
    public static final ConfigHotkey SpawnBotForItem = ofHotkey("召唤物品对应假人", "", KeybindSettings.GUI, "对物品按下快捷键可召唤对应假人\n使用/dcreload item_bot_mapping以加载映射");
    public static final ConfigHotkey SyncContainer = ofHotkey("容器同步", "", ANY, "以当前容器为模板, 将选区内同类容器按模板修改\n再次按下将重置\n思路来自宅咸鱼, 代码独立实现");


    // toggle
    public static final ConfigBooleanHotkeyed StartMassCrafting = ofBooleanHotkeyed("启动连续喷射合成", false, "", ANY);
    public static final ConfigBooleanHotkeyed StartStoneCutting = ofBooleanHotkeyed("启动连续切石", false, "", ANY);
    public static final ConfigBooleanHotkeyed AutoThrow = ofBooleanHotkeyed("自动丢弃", false, "", "白名单中的物品会被丢出\n在GUI中不生效");
    public static final ConfigBooleanHotkeyed AutoContainerTaker = ofBooleanHotkeyed("自动从容器取出", false, "", "若完全取出, 自动关闭GUI");

    public static final List<IConfigBase> VALUES;
    public static final List<IConfigBase> LISTS;
    public static final List<IHotkey> HOTKEY;
    public static final List<IHotkey> TOGGLE;

    public static final List<IConfigBase> ALL_CONFIGS;

    public InventoryConfig(Path path, List<? extends IConfigBase> configs) {
        super(path, configs);
    }

    public static InventoryConfig getInstance() {
        return INSTANCE;
    }

    static {
        VALUES = ImmutableList.of(
                SwitchPreset,
                SortingContainersLast,
                CachedSorting,
                ItemSortingOrder,
                MassCraftingMode
        );
        LISTS = ImmutableList.of(
                AutoThrowWhiteList
        );
        HOTKEY = ImmutableList.of(
                SortInventory,
                StoneCutterRecipeView,
                StoreStoneCutterRecipe,
                CutStone,
                CutStoneAndThrow,
                MyMassCrafting,
                ThrowSection,
                ThrowSame,
                SpawnBotForItem,
                ModifierMoveAll,
                ModifierMoveStack,
                ModifierMoveSame,
                ModifierSpreadItem,
                ModifierClearBundle,
                SyncContainer
        );
        TOGGLE = ImmutableList.of(
                StartMassCrafting,
                StartStoneCutting,
                AutoThrow,
                AutoContainerTaker
        );

        ImmutableList.Builder<IConfigBase> builder = ImmutableList.builder();
        builder.addAll(VALUES);
        builder.addAll(LISTS);
        builder.addAll(HOTKEY);
        builder.addAll(TOGGLE);
        ALL_CONFIGS = builder.build();

        INSTANCE = new InventoryConfig(DebrisClient.CONFIG_DIR.resolve("config_inventory.json"), ALL_CONFIGS);
    }
}
