package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.feat.HeartType;
import com.github.debris.debrisclient.inventory.sort.SortCategory;
import com.github.debris.debrisclient.unsafe.itemScroller.MassCraftingImpl;
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
import java.nio.file.Path;
import java.util.List;

import static com.github.debris.debrisclient.DebrisClient.MOD_NAME;
import static com.github.debris.debrisclient.config.ConfigFactory.*;

public class DCCommonConfig implements IConfigHandler {
    private static final DCCommonConfig INSTANCE = new DCCommonConfig();
    private static final Path FILE_PATH = DebrisClient.CONFIG_DIR.resolve("config_common.json");


    // value
    public static final ConfigBoolean SortingContainersLast = ofBoolean("整理时容器置于末端", true, "潜影盒, 收纳袋");
    public static final ConfigBoolean CachedSorting = ofBoolean("整理时使用缓存算法", true, "相比直接操作, 可减少发包");
    public static final ConfigEnum<SortCategory> ItemSortingOrder = ofEnum("物品整理顺序", SortCategory.CREATIVE_INVENTORY, "1.翻译键顺序\n2.按创造模式物品栏顺序\n3.按翻译后名称顺序\n4.按拼音顺序(需要Rei)");
    public static final ConfigEnum<MassCraftingImpl> MassCraftingMode = ofEnum("喷射合成实现", MassCraftingImpl.RECIPE_BOOK, "配方书依赖服务器,较慢但不出错\n手动依赖客户端,可能与服务器不同步导致合成错误");
    public static final ConfigInteger TriggerButtonOffset = ofInteger("触发按钮的坐标偏移", 42, -100, 100, true, "自动对齐可能有问题");
    public static final ConfigInteger AutoRepeatAntiDDos = ofInteger("自动复读防刷屏", Integer.MAX_VALUE, 1, Integer.MAX_VALUE, false, "1秒内同一条消息被发送次数超过阈值时, 将取消之后的发送");
    public static final ConfigBoolean FullDebugInfo = ofBoolean("完整调试权限", false);
    public static final ConfigEnum<HeartType> HeartTypeOverride = ofEnum("生命值样式覆写", HeartType.NONE);
    public static final ConfigBoolean ExtraTooltip = ofBoolean("额外物品提示", false, "对于非满级附魔,标注其最高等级\n铁砧惩罚,在铁砧操作时所需等级\n附魔价值,将其全部魔咒转移时所需等级");
    public static final ConfigInteger InteractContainerPeriod = ofInteger("交互容器间隔", 3, 0, 100, true, "按刻计\n高延迟服务器内应调高此项");
    public static final ConfigBoolean RetroDefaultSkin = ofBoolean("怀旧默认皮肤", false, "仅Steve, Alex");


    // key settings
    private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);
    private static final KeybindSettings ANY = KeybindSettings.create(KeybindSettings.Context.ANY, KeyAction.PRESS, false, true, false, true);


    // compat
    public static final ConfigBoolean FreeCamKeepAutoMoving = ofBoolean("灵魂出窍时允许自动移动", true, "本模组的自动移动, 在灵魂出窍时会默认停止移动");
    public static final ConfigBoolean FreeCamSpectatorFix = ofBoolean("旁观模式灵魂出窍修复", true, "当你附身别的生物, 启动灵魂出窍时相机仍在附身地");
    public static final ConfigBoolean RetroFreeCam = ofBoolean("怀旧灵魂出窍", false, "适用于tweakeroo0.24.1");
    public static final ConfigBoolean ToolSwitchFix = ofBoolean("工具切换修复", true, "无合适工具时, 不应切换到第一个快捷栏");
    public static final ConfigBoolean ProgressResuming = ofBoolean("进度恢复", true, "打开配置页面时, 能跳转上次进度\n对MaLiLib驱动的模组和CommandButton有效");
    public static final ConfigBoolean WorldEditVisibility = ofBoolean("WorldEdit可视化", false, "作为WECUI的暂时替代, 仅支持长方体选区, 且渲染需要litematica");
    public static final ConfigColor WorldEditOverlay = ofColor("WorldEdit滤镜", "#30FFFF00", "在WE选区渲染后再加上, 以区分litematica的选区");
    public static final ConfigBoolean InventoryPreviewSupportComparator = ofBoolean("物品栏预览支持比较器", true);
    public static final ConfigBoolean PinYinSearch = ofBoolean("拼音搜索", false, "需要Rei, 支持由MaLiLib驱动的模组, 创造模式物品栏, 配方书");
    public static final ConfigBoolean CommentSearch = ofBoolean("注释搜索", false, "对MaLiLib驱动的模组有效");
    public static final ConfigBoolean GlobalConfigEnhance = ofBoolean("全局配置加强", false, "将本模组的配置加强应用到所有masa模组,包含以下功能:\n为热键添加触发按钮\n为枚举列表提供预览");
    public static final ConfigBoolean ScrollerEnhance = ofBoolean("滑动条改进", true, "masa驱动\n允许点击白块之外拖动");
    public static final ConfigBoolean XRayAutoColor = ofBoolean("XRay自动取色", true);
    public static final ConfigBoolean WthitMasaCompat = ofBoolean("Wthit与Masa兼容", true, "在合适的时机不渲染tooltip");
    public static final ConfigBoolean DisableREIWarning = ofBoolean("禁用REI警告", false, "至少在18.0.796版本仍然每次进服都在弹窗");


    // list
    public static final ConfigStringList AutoRepeatPlayerList = ofStringList("自动复读玩家列表");
    public static final ConfigStringList AutoRepeatBlackList = ofStringList("自动复读字符串黑名单", ImmutableList.of(), "可用样式如下:\n直接取消复读,如\"debris\"\n箭头->表示替换,如\"debris->spirit\"");
    public static final ConfigStringList AutoThrowWhiteList = ofStringList("自动丢弃白名单");
    public static final ConfigStringList CullBlockEntityList = ofStringList("剔除方块实体列表");
    public static final ConfigStringList CullEntityList = ofStringList("剔除实体渲染列表");
    public static final ConfigStringList CullParticleList = ofStringList("剔除粒子列表");
    public static final ConfigStringList MuteSoundList = ofStringList("静音音效列表");
    public static final ConfigStringList HighlightEntityList = ofStringList("高亮实体列表");


    // key
    public static final ConfigHotkey OpenWindow = ofHotkey("打开设置菜单", "D,C", "打开设置菜单");
    public static final ConfigHotkey OpenUniversalSearch = ofHotkey("打开全局搜索", "", "masa驱动");
    public static final ConfigHotkey SortItem = ofHotkey("整理物品", "", KeybindSettings.GUI, "按区域进行\n兼容carpet假人不会乱点按钮\n兼容创造模式物品栏");
    public static final ConfigHotkey StoneCutterRecipeView = ofHotkey("展示切石机配方", "A", GUI_RELAXED);
    public static final ConfigHotkey StoreStoneCutterRecipe = ofHotkey("储存切石机配方", "BUTTON_3", GUI_RELAXED_CANCEL);
    public static final ConfigHotkey CutStone = ofHotkey("切石", "LEFT_CONTROL, C", GUI_NO_ORDER);
    public static final ConfigHotkey CutStoneThenThrow = ofHotkey("切石并丢出", "LEFT_CONTROL,LEFT_ALT,C", GUI_NO_ORDER);
    public static final ConfigHotkey MyMassCrafting = ofHotkey("我的喷射合成", "", GUI_NO_ORDER, "作为ItemScroller的替代品\n虽然仍然需要安装它才能用(以便读取配方)\n而且需要较高版本");
    public static final ConfigHotkey ThrowSection = ofHotkey("清空区域", "", KeybindSettings.GUI, "全部丢出");
    public static final ConfigHotkey ThrowSimilar = ofHotkey("丢出类似", "", KeybindSettings.GUI);
    public static final ConfigHotkey KickBot = ofHotkey("踢出假人", "", KeybindSettings.PRESS_ALLOWEXTRA, "按住时踢出准心所指假人\n支持灵魂出窍");
    public static final ConfigHotkey RestoreKicking = ofHotkey("假人复原", "", "召回误踢的假人");
    public static final ConfigHotkey BotSpawnCommand = ofHotkey("假人召唤指令", "", "在聊天栏中建议当前位置");
    public static final ConfigHotkey ModifierMoveAll = ofHotkey("移动全部:修饰键", "", GUI_RELAXED_CANCEL, "按住时左键会移动当前区域全部\n兼容carpet假人不会乱点按钮");
    public static final ConfigHotkey ModifierSpreadItem = ofHotkey("分散物品:修饰键", "", GUI_RELAXED_CANCEL, "按住时点击会尝试将手中物品均分到点击区域全部槽位");
    public static final ConfigHotkey ModifierMoveSimilar = ofHotkey("移动类似:修饰键", "", GUI_RELAXED_CANCEL, "按住时左键会移动当前区域类似物品");
    public static final ConfigHotkey ModifierClearBundle = ofHotkey("清空收纳袋:修饰键", "", GUI_RELAXED_CANCEL, "");
    public static final ConfigHotkey ResendLastChat = ofHotkey("重发上一条消息", "", "相当于按UP键");
    public static final ConfigHotkey RepeatNewestChat = ofHotkey("消息复读", "", "复读聊天栏中最新消息");
    public static final ConfigHotkey AlignWithEnderEye = ofHotkey("对齐末影之眼", "");
    public static final ConfigHotkey ModifierFreeCamInput = ofHotkey("灵魂出窍输入:修饰键", "", "按住时输入将对实际画面生效\n仍需开启tweakeroo中的灵魂出窍用户输入");
    public static final ConfigHotkey TakeOff = ofHotkey("起飞", "", "使用鞘翅和烟花火箭起飞");
    public static final ConfigHotkey SyncContainer = ofHotkey("容器同步", "", ANY, "以当前容器为模板, 将选区内同类容器按模板修改\n再次按下将重置\n思路来自宅咸鱼, 代码独立实现");
    public static final ConfigHotkey OpenSelectionContainers = ofHotkey("打开选区内容器", "", ANY, "记录列表, 之后逐个打开");
    public static final ConfigHotkey InteractSelectionEntities = ofHotkey("交互选区内实体", "", ANY, "记录列表, 之后逐个交互");


    public static final ConfigHotkey TEST = ofHotkey("测试", "", KeybindSettings.GUI);


    // toggle
    public static final ConfigBooleanHotkeyed AUTO_WALK = ofBooleanHotkeyed("自动前进", false, "LEFT_ALT,UP", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_LEFT = ofBooleanHotkeyed("自动向左", false, "LEFT_ALT,LEFT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_BACK = ofBooleanHotkeyed("自动后退", false, "LEFT_ALT,DOWN", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_RIGHT = ofBooleanHotkeyed("自动向右", false, "LEFT_ALT,RIGHT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed MonitorPortalGeneration = ofBooleanHotkeyed("监听传送门生成", false, "", "限单机游戏");
    public static final ConfigBooleanHotkeyed MonitorThunderWeather = ofBooleanHotkeyed("监听雷暴天气", false, "");
    public static final ConfigBooleanHotkeyed StartStoneCutting = ofBooleanHotkeyed("启动连续切石", false, "", ANY);
    public static final ConfigBooleanHotkeyed StartMassCrafting = ofBooleanHotkeyed("启动连续喷射合成", false, "", ANY);
    public static final ConfigBooleanHotkeyed LoyalerTrident = ofBooleanHotkeyed("更忠诚的三叉戟", false, "", "发射的忠诚三叉戟能够回到副手");
    public static final ConfigBooleanHotkeyed PathNodesVisibility = ofBooleanHotkeyed("寻路节点可视化", false, "");
    public static final ConfigBooleanHotkeyed PathNodesOnlyNamed = ofBooleanHotkeyed("寻路节点仅限命名生物", false, "");
    public static final ConfigBooleanHotkeyed AutoThrow = ofBooleanHotkeyed("自动丢弃", false, "", "在GUI中不生效");
    public static final ConfigBooleanHotkeyed AutoContainerTaker = ofBooleanHotkeyed("自动从容器取出", false, "", "若完全取出, 自动关闭GUI");
    public static final ConfigBooleanHotkeyed AutoExtinguisher = ofBooleanHotkeyed("自动灭火", false, "", "不影响灵魂火");
    public static final ConfigBooleanHotkeyed AutoBulletCatching = ofBooleanHotkeyed("自动接子弹", false, "", "潜影贝, 恶魂");


    // yeet
    public static final ConfigBooleanHotkeyed CullSign = ofBooleanHotkeyed("剔除告示牌", false, "");
    public static final ConfigBooleanHotkeyed CullChest = ofBooleanHotkeyed("剔除箱子", false, "");
    public static final ConfigBooleanHotkeyed CullItemFrame = ofBooleanHotkeyed("剔除物品展示框", false, "");
    public static final ConfigBooleanHotkeyed CullItemEntity = ofBooleanHotkeyed("剔除物品实体", false, "");
    public static final ConfigBooleanHotkeyed CullExperienceOrb = ofBooleanHotkeyed("剔除经验球", false, "");
    public static final ConfigBooleanHotkeyed DarknessOverride = ofBooleanHotkeyed("禁用失明和黑暗", false, "");
    public static final ConfigBooleanHotkeyed MuteExplosion = ofBooleanHotkeyed("爆炸静音", false, "", "不包括龙息爆炸");
    public static final ConfigBooleanHotkeyed MuteWither = ofBooleanHotkeyed("凋灵静音", false, "");
    public static final ConfigBooleanHotkeyed MuteEnderman = ofBooleanHotkeyed("末影人静音", false, "");
    public static final ConfigBooleanHotkeyed MuteZombifiedPiglin = ofBooleanHotkeyed("僵尸猪人静音", false, "");
    public static final ConfigBooleanHotkeyed MuteGuardian = ofBooleanHotkeyed("守卫者静音", false, "");
    public static final ConfigBooleanHotkeyed MuteMinecart = ofBooleanHotkeyed("矿车静音", false, "");
    public static final ConfigBooleanHotkeyed MuteThunder = ofBooleanHotkeyed("雷声静音", false, "");
    public static final ConfigBooleanHotkeyed MuteDispenser = ofBooleanHotkeyed("发射器静音", false, "", "包括投掷器, 仅屏蔽发射失败音效");
    public static final ConfigBooleanHotkeyed MuteAnvil = ofBooleanHotkeyed("铁砧静音", false, "");
    public static final ConfigBooleanHotkeyed MuteDoor = ofBooleanHotkeyed("门静音", false, "", "包括任何门");
    public static final ConfigBooleanHotkeyed CullPoofParticle = ofBooleanHotkeyed("剔除生物死亡粒子", false, "", "即poof, 详见wiki");
    public static final ConfigBooleanHotkeyed BlockBreakingCooldownOverride = ofBooleanHotkeyed("禁用方块挖掘冷却", false, "", "不影响创造模式");
    public static final ConfigBooleanHotkeyed MuteGLDebugInfo = ofBooleanHotkeyed("禁止打印GL调试信息", false, "有时一直在后台打印, 且难以确定错误原因");
    public static final ConfigBooleanHotkeyed CullFireAnimation = ofBooleanHotkeyed("剔除火焰动画", false, "");
    public static final ConfigBooleanHotkeyed CullArmor = ofBooleanHotkeyed("剔除盔甲", false, "");


    // highlight
    public static final ConfigBooleanHotkeyed ForceRenderEndGatewayBeam = ofBooleanHotkeyed("强制渲染末地折跃门光柱", false, "");
    public static final ConfigBooleanHotkeyed HighlightAll = ofBooleanHotkeyed("高亮全部实体", false, "");
    public static final ConfigBooleanHotkeyed HighlightBlaze = ofBooleanHotkeyed("高亮烈焰人", false, "");
    public static final ConfigBooleanHotkeyed HighlightCreeper = ofBooleanHotkeyed("高亮苦力怕", false, "");
    public static final ConfigBooleanHotkeyed HighlightEnderman = ofBooleanHotkeyed("高亮末影人", false, "");
    public static final ConfigBooleanHotkeyed HighlightItem = ofBooleanHotkeyed("高亮物品", false, "");
    public static final ConfigBooleanHotkeyed HighlightPiglinBrute = ofBooleanHotkeyed("高亮猪灵蛮兵", false, "");
    public static final ConfigBooleanHotkeyed HighlightPlayer = ofBooleanHotkeyed("高亮玩家", false, "");
    public static final ConfigBooleanHotkeyed HighlightWanderingTrader = ofBooleanHotkeyed("高亮流浪商人", false, "");
    public static final ConfigBooleanHotkeyed HighlightWitherSkeleton = ofBooleanHotkeyed("高亮凋零骷髅", false, "");


    public static final List<IConfigBase> ALL_CONFIGS;


    public static final ImmutableList<IConfigBase> Values;
    public static final ImmutableList<IConfigBase> Compat;
    public static final ImmutableList<IConfigBase> Lists;
    public static final ImmutableList<ConfigHotkey> KeyPress;
    public static final ImmutableList<IHotkeyTogglable> KeyToggle;
    public static final ImmutableList<IHotkeyTogglable> Yeets;
    public static final ImmutableList<IHotkeyTogglable> Highlights;

    public static DCCommonConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public void load() {
        File settingFile = FILE_PATH.toFile();
        if (settingFile.isFile() && settingFile.exists()) {
            JsonElement jsonElement = JsonUtils.parseJsonFile(settingFile);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(obj, MOD_NAME, ALL_CONFIGS);
            }
        }
    }

    @Override
    public void save() {
        File folder = DebrisClient.CONFIG_DIR.toFile();
        if ((folder.exists() && folder.isDirectory()) || folder.mkdirs()) {
            JsonObject configRoot = new JsonObject();
            ConfigUtils.writeConfigBase(configRoot, MOD_NAME, ALL_CONFIGS);
            JsonUtils.writeJsonToFile(configRoot, FILE_PATH.toFile());
        }
    }

    private static ImmutableList<IConfigBase> buildCompat() {
        ImmutableList.Builder<IConfigBase> builder = ImmutableList.builder();
        builder.add(ProgressResuming, PinYinSearch, CommentSearch, GlobalConfigEnhance, ScrollerEnhance);
        if (ModReference.hasMod(ModReference.Tweakeroo)) {
            builder.add(FreeCamKeepAutoMoving, FreeCamSpectatorFix, RetroFreeCam, ToolSwitchFix);
        }
        if (ModReference.hasMod(ModReference.MiniHud) && ModReference.hasMod(ModReference.MagicLibMCApi)) {
            builder.add(InventoryPreviewSupportComparator);
        }
        if (ModReference.hasMod(ModReference.WorldEdit) && ModReference.hasMod(ModReference.Litematica)) {
            builder.add(WorldEditVisibility);
            builder.add(WorldEditOverlay);
        }
        if (ModReference.hasMod(ModReference.XRay)) {
            builder.add(XRayAutoColor);
        }
        if (ModReference.hasMod(ModReference.Wthit)) {
            builder.add(WthitMasaCompat);
        }
        if (ModReference.hasMod(ModReference.REI)) {
            builder.add(DisableREIWarning);
        }
        return builder.build();
    }

    static {
        Values = ImmutableList.of(
                SortingContainersLast,
                CachedSorting,
                ItemSortingOrder,
                MassCraftingMode,
                TriggerButtonOffset,
                AutoRepeatAntiDDos,
                FullDebugInfo,
                HeartTypeOverride,
                ExtraTooltip,
                InteractContainerPeriod,
                RetroDefaultSkin
        );
        Compat = buildCompat();
        Lists = ImmutableList.of(
                AutoRepeatPlayerList,
                AutoRepeatBlackList,
                AutoThrowWhiteList,
                CullBlockEntityList,
                CullEntityList,
                CullParticleList,
                MuteSoundList,
                HighlightEntityList
        );
        KeyPress = ImmutableList.of(
                OpenWindow,
                OpenUniversalSearch,
                SortItem,
                StoneCutterRecipeView,
                StoreStoneCutterRecipe,
                CutStone,
                CutStoneThenThrow,
                MyMassCrafting,
                ThrowSection,
                ThrowSimilar,
                KickBot,
                RestoreKicking,
                BotSpawnCommand,
                ModifierMoveAll,
                ModifierMoveSimilar,
                ModifierSpreadItem,
                ModifierClearBundle,
                ResendLastChat,
                RepeatNewestChat,
                AlignWithEnderEye,
                ModifierFreeCamInput,
                TakeOff,
                SyncContainer,
                OpenSelectionContainers,
                InteractSelectionEntities,
                TEST
        );
        KeyToggle = ImmutableList.of(
                AUTO_WALK,
                AUTO_LEFT,
                AUTO_RIGHT,
                AUTO_BACK,
                MonitorPortalGeneration,
                MonitorThunderWeather,
                StartStoneCutting,
                StartMassCrafting,
                LoyalerTrident,
                PathNodesVisibility,
                PathNodesOnlyNamed,
                AutoThrow,
                AutoContainerTaker,
                AutoExtinguisher,
                AutoBulletCatching
        );
        Yeets = ImmutableList.of(
                CullSign,
                CullChest,
                CullItemFrame,
                CullItemEntity,
                CullExperienceOrb,
                DarknessOverride,
                MuteExplosion,
                MuteWither,
                MuteEnderman,
                MuteZombifiedPiglin,
                MuteGuardian,
                MuteMinecart,
                MuteThunder,
                MuteDispenser,
                MuteAnvil,
                MuteDoor,
                CullPoofParticle,
                BlockBreakingCooldownOverride,
                MuteGLDebugInfo,
                CullFireAnimation,
                CullArmor
        );
        Highlights = ImmutableList.of(
                ForceRenderEndGatewayBeam,
                HighlightAll,
                HighlightBlaze,
                HighlightCreeper,
                HighlightEnderman,
                HighlightItem,
                HighlightPiglinBrute,
                HighlightPlayer,
                HighlightWanderingTrader,
                HighlightWitherSkeleton
        );
        ImmutableList.Builder<IConfigBase> builder = ImmutableList.builder();
        builder.addAll(Values);
        builder.addAll(Compat);
        builder.addAll(Lists);
        builder.addAll(KeyToggle);
        builder.addAll(KeyPress);
        builder.addAll(Yeets);
        builder.addAll(Highlights);
        ALL_CONFIGS = builder.build();
        INSTANCE.load();
    }
}
