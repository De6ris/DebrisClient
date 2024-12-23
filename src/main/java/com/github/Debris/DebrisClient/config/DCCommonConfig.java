package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.DebrisClient;
import com.github.Debris.DebrisClient.config.options.ConfigHotKeyExtend;
import com.github.Debris.DebrisClient.config.options.ConfigOptionListExtend;
import com.github.Debris.DebrisClient.inventory.sort.SortCategory;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.List;

import static com.github.Debris.DebrisClient.DebrisClient.MOD_ID;

public class DCCommonConfig implements IConfigHandler {
    private static final DCCommonConfig INSTANCE = new DCCommonConfig();
    private static final String FILE_PATH = DebrisClient.CONFIG_DIR + "config_common.json";
    private static final File CONFIG_DIR = new File(DebrisClient.CONFIG_DIR);


    // value
    public static final ConfigBoolean SortingBoxesLast = new ConfigBoolean("整理时潜影盒置于末端", true, "");
    public static final ConfigBoolean CachedSorting = new ConfigBoolean("整理时使用缓存算法", true, "相比直接操作, 可减少发包");
    public static final ConfigOptionList ItemSortingOrder = new ConfigOptionListExtend("物品整理顺序", SortCategory.CREATIVE_INVENTORY, "1.翻译键顺序\n2.按创造模式物品栏顺序\n3.按翻译后名称顺序\n4.按拼音顺序(需要Rei)");
    public static final ConfigBoolean UseRecipeBookToCraft = new ConfigBoolean("喷射合成使用配方书", true, "可能比较的优雅");
    public static final ConfigBoolean Use64Q = new ConfigBoolean("使用64次Q扔出合成结果", true, "这样不必让物品栏腾出空间\n减少无关物品丢出\n但会导致发包较多\n1.22+将支持Ctrl丢出");
    public static final ConfigInteger TriggerButtonOffset = new ConfigInteger("触发按钮的坐标偏移", 42, -100, 100, true, "自动对齐可能有问题");
    public static final ConfigBoolean ProgressResuming = new ConfigBoolean("进度恢复", true, "打开配置页面时, 能跳转上次进度");
    public static final ConfigStringList TradingTargets = new ConfigStringList("定向交易目标", ImmutableList.of(), "需打开定向自动交易功能");
    public static final ConfigStringList AutoRepeatBlackList = new ConfigStringList("自动复读字符串黑名单", ImmutableList.of(), "关于自动复读:\n首先你需要安装有clientcommands或者clientarguments模组\n在指令中输入dc即可自动补全");
    public static final ConfigBoolean AutoRepeatAntiDDos = new ConfigBoolean("自动复读防刷屏", false, "1秒内同一条消息被发送次数超过阈值时, 将取消之后的发送");
    public static final ConfigInteger AutoRepeatAntiDDosThreshold = new ConfigInteger("自动复读刷屏阈值", 4, 1, 16, "");


    // key settings
    private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);


    // fix
    public static final ConfigBoolean FreeCamKeepAutoMoving = new ConfigBoolean("灵魂出窍时允许自动移动", true, "本模组的自动移动, 在灵魂出窍时会默认停止移动");
    public static final ConfigBoolean CarpetSinglePlayerFix = new ConfigBoolean("Carpet单人崩溃修复", true, "仅1.21有效, 更改后重启有效");
    public static final ConfigBoolean FreeCamSpectatorFix = new ConfigBoolean("旁观模式灵魂出窍修复", true, "当你附身别的生物, 启动灵魂出窍时相机仍在附身地");


    // key
    public static final ConfigHotkey OpenWindow = new ConfigHotKeyExtend("打开设置菜单", "D,C", "打开设置菜单");
    public static final ConfigHotkey ReloadCommandButton = new ConfigHotKeyExtend("重载CommandButton", "", "需要有CommandButton模组");
    public static final ConfigHotkey SortItem = new ConfigHotKeyExtend("整理物品", "R", KeybindSettings.GUI, "按区域进行\n兼容carpet假人不会乱点按钮\n兼容创造模式物品栏");
    public static final ConfigHotkey StoneCutterRecipeView = new ConfigHotKeyExtend("展示切石机配方", "A", GUI_RELAXED, "");
    public static final ConfigHotkey StoreStoneCutterRecipe = new ConfigHotKeyExtend("储存切石机配方", "BUTTON_3", GUI_RELAXED_CANCEL, "");
    public static final ConfigHotkey CutStone = new ConfigHotKeyExtend("切石", "LEFT_CONTROL, C", GUI_NO_ORDER, "");
    public static final ConfigHotkey CutStoneThenDrop = new ConfigHotKeyExtend("切石并丢出", "LEFT_CONTROL,LEFT_ALT,C", GUI_NO_ORDER, "");
    public static final ConfigHotkey MyMassCrafting = new ConfigHotKeyExtend("我的喷射合成", "", GUI_NO_ORDER, "作为ItemScroller的替代品\n虽然仍然需要安装它才能用(以便读取配方)\n而且需要较高版本");
    public static final ConfigHotkey KickBot = new ConfigHotKeyExtend("踢出假人", "", KeybindSettings.PRESS_ALLOWEXTRA, "按住时踢出准心所指假人\n支持灵魂出窍");
    public static final ConfigHotkey RestoreKicking = new ConfigHotKeyExtend("假人复原", "", "召回误踢的假人");
    public static final ConfigHotkey ModifierMoveAll = new ConfigHotKeyExtend("移动全部:修饰键", "SPACE", GUI_RELAXED_CANCEL, "按住时左键会移动当前区域全部\n兼容carpet假人不会乱点按钮");
    public static final ConfigHotkey ResendLastChat = new ConfigHotKeyExtend("重发上一条消息", "", "相当于按UP键");
    public static final ConfigHotkey RepeatNewestChat = new ConfigHotKeyExtend("消息复读", "", "复读聊天栏中最新消息");
    public static final ConfigHotkey QuickDataGet = new ConfigHotKeyExtend("快速DataGet指令","","准心的方块或实体");


    public static final ConfigHotkey TEST = new ConfigHotKeyExtend("测试", "", KeybindSettings.GUI, "测试");
    public static final ConfigHotkey TEST2 = new ConfigHotKeyExtend("测试2", "RIGHT_ALT", KeybindSettings.GUI, "测试2");


    // toggle
    public static final ConfigBooleanHotkeyed AUTO_WALK = new ConfigBooleanHotkeyed("自动前进", false, "LEFT_ALT,UP", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_LEFT = new ConfigBooleanHotkeyed("自动向左", false, "LEFT_ALT,LEFT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_BACK = new ConfigBooleanHotkeyed("自动后退", false, "LEFT_ALT,DOWN", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_RIGHT = new ConfigBooleanHotkeyed("自动向右", false, "LEFT_ALT,RIGHT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed MonitorPortalGeneration = new ConfigBooleanHotkeyed("监听传送门生成", false, "", "");
    public static final ConfigBooleanHotkeyed StartStoneCutting = new ConfigBooleanHotkeyed("启动连续切石", false, "", KeybindSettings.INGAME_BOTH, "", "启动连续切石");
    public static final ConfigBooleanHotkeyed StartMassCrafting = new ConfigBooleanHotkeyed("启动连续喷射合成", false, "", KeybindSettings.INGAME_BOTH, "", "启动连续喷射合成");
    public static final ConfigBooleanHotkeyed AutoGuiQuitting = new ConfigBooleanHotkeyed("自动关闭容器GUI", false, "", KeybindSettings.INGAME_BOTH, "不会关闭容器之外的GUI", "自动关闭GUI");
    public static final ConfigBooleanHotkeyed OrientedAutoTrading = new ConfigBooleanHotkeyed("定向自动交易", false, "", "打开交易GUI时自动交易所有在名单上的物品");
    public static final ConfigBooleanHotkeyed LoyalerTrident = new ConfigBooleanHotkeyed("更忠诚的三叉戟", false, "", "发射的忠诚三叉戟能够回到副手");
    public static final ConfigBooleanHotkeyed PathNodesVisibility = new ConfigBooleanHotkeyed("寻路节点可视化", false, "", "");
    public static final ConfigBooleanHotkeyed PathNodesOnlyNamed = new ConfigBooleanHotkeyed("寻路节点仅限命名生物", false, "", "");


    // yeet
    public static final ConfigBooleanHotkeyed CancelSignRendering = new ConfigBooleanHotkeyed("取消告示牌渲染", false, "", "");
    public static final ConfigBooleanHotkeyed CancelFrameRendering = new ConfigBooleanHotkeyed("取消物品展示框渲染", false, "", "");
    public static final ConfigBooleanHotkeyed DarknessOverride = new ConfigBooleanHotkeyed("禁用失明和黑暗", false, "", "");


    public static final List<IConfigBase> ALL_CONFIGS;


    public static final ImmutableList<IConfigBase> Values;
    public static final ImmutableList<IConfigBase> Fix;
    public static final ImmutableList<ConfigHotkey> KeyPress;
    public static final ImmutableList<IHotkeyTogglable> KeyToggle;
    public static final ImmutableList<IHotkeyTogglable> Yeets;

    public static DCCommonConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public void load() {
        File settingFile = new File(FILE_PATH);
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, MOD_ID, ALL_CONFIGS);
            }
        }
    }

    @Override
    public void save() {
        if ((CONFIG_DIR.exists() && CONFIG_DIR.isDirectory()) || CONFIG_DIR.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            ConfigUtils.writeConfigBase(configRoot, MOD_ID, ALL_CONFIGS);
            JsonUtils.writeJsonToFile(configRoot, new File(FILE_PATH));
        }
    }

    static {
        Values = ImmutableList.of(
                SortingBoxesLast,
                CachedSorting,
                ItemSortingOrder,
                UseRecipeBookToCraft,
                Use64Q,
                TriggerButtonOffset,
                ProgressResuming,
                TradingTargets,
                AutoRepeatBlackList,
                AutoRepeatAntiDDos,
                AutoRepeatAntiDDosThreshold
        );
        Fix = ImmutableList.of(
                FreeCamKeepAutoMoving,
                CarpetSinglePlayerFix,
                FreeCamSpectatorFix
        );
        KeyPress = ImmutableList.of(
                OpenWindow,
                ReloadCommandButton,
                SortItem,
                StoneCutterRecipeView,
                StoreStoneCutterRecipe,
                CutStone,
                CutStoneThenDrop,
                MyMassCrafting,
                KickBot,
                RestoreKicking,
                ModifierMoveAll,
                ResendLastChat,
                RepeatNewestChat,
                QuickDataGet,
                TEST,
                TEST2
        );
        KeyToggle = ImmutableList.of(
                AUTO_WALK,
                AUTO_LEFT,
                AUTO_RIGHT,
                AUTO_BACK,
                MonitorPortalGeneration,
                StartStoneCutting,
                StartMassCrafting,
                AutoGuiQuitting,
                OrientedAutoTrading,
                LoyalerTrident,
                PathNodesVisibility,
                PathNodesOnlyNamed
        );
        Yeets = ImmutableList.of(
                CancelSignRendering,
                CancelFrameRendering,
                DarknessOverride
        );
        ImmutableList.Builder<IConfigBase> builder = ImmutableList.builder();
        builder.addAll(Values);
        builder.addAll(Fix);
        builder.addAll(KeyToggle);
        builder.addAll(KeyPress);
        builder.addAll(Yeets);
        ALL_CONFIGS = builder.build();
        INSTANCE.load();
    }
}
