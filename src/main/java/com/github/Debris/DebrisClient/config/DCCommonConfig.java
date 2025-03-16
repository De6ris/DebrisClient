package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.DebrisClient;
import com.github.Debris.DebrisClient.config.options.ConfigEnum;
import com.github.Debris.DebrisClient.feat.AutoRepeat;
import com.github.Debris.DebrisClient.inventory.sort.SortCategory;
import com.github.Debris.DebrisClient.unsafe.itemScroller.MassCraftingImpl;
import com.github.Debris.DebrisClient.util.HeartType;
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
import static com.github.Debris.DebrisClient.config.ConfigFactory.*;

public class DCCommonConfig implements IConfigHandler {
    private static final DCCommonConfig INSTANCE = new DCCommonConfig();
    private static final String FILE_PATH = DebrisClient.CONFIG_DIR + "config_common.json";
    private static final File CONFIG_DIR = new File(DebrisClient.CONFIG_DIR);


    // value
    public static final ConfigBoolean SortingContainersLast = ofBoolean("整理时容器置于末端", true, "潜影盒, 收纳袋");
    public static final ConfigBoolean CachedSorting = ofBoolean("整理时使用缓存算法", true, "相比直接操作, 可减少发包");
    public static final ConfigEnum<SortCategory> ItemSortingOrder = ofEnum("物品整理顺序", SortCategory.CREATIVE_INVENTORY, "1.翻译键顺序\n2.按创造模式物品栏顺序\n3.按翻译后名称顺序\n4.按拼音顺序(需要Rei)");
    public static final ConfigEnum<MassCraftingImpl> MassCraftingMode = ofEnum("喷射合成实现", MassCraftingImpl.RECIPE_BOOK, "配方书依赖服务器,较慢但不出错\n手动依赖客户端,可能与服务器不同步导致合成错误");
    public static final ConfigInteger TriggerButtonOffset = ofInteger("触发按钮的坐标偏移", 42, -100, 100, true, "自动对齐可能有问题");
    public static final ConfigBoolean ProgressResuming = ofBoolean("进度恢复", true, "打开配置页面时, 能跳转上次进度\n对MaLiLib驱动的模组和CommandButton有效");
    public static final ConfigEnum<AutoRepeat.BlackListMode> AutoRepeatBlackListMode = ofEnum("自动复读字符串黑名单模式", AutoRepeat.BlackListMode.CANCEL);
    public static final ConfigString AutoRepeatBlackListReplace = ofString("自动复读字符串替换", "");
    public static final ConfigBoolean AutoRepeatAntiDDos = ofBoolean("自动复读防刷屏", false, "1秒内同一条消息被发送次数超过阈值时, 将取消之后的发送");
    public static final ConfigInteger AutoRepeatAntiDDosThreshold = ofInteger("自动复读刷屏阈值", 4, 1, 16);
    public static final ConfigColor WorldEditOverlay = ofColor("WorldEdit滤镜", "#30FFFF00", "在WE选区渲染后再加上, 以区分litematica的选区");
    public static final ConfigBoolean InventoryPreviewSupportComparator = ofBoolean("物品栏预览支持比较器", true, "需要MiniHud和MagicLib,因为MasaGadget未更新,以此暂代");
    public static final ConfigBoolean PinYinSearch = ofBoolean("拼音搜索", false, "需要Rei, 支持由MaLiLib驱动的模组, 创造模式物品栏, 配方书");
    public static final ConfigBoolean CommentSearch = ofBoolean("注释搜索", false, "对MaLiLib驱动的模组有效");
    public static final ConfigBoolean FullDebugInfo = ofBoolean("完整调试权限", false);
    public static final ConfigBoolean XRayAutoColor = ofBoolean("XRay自动取色", false);
    public static final ConfigBoolean WthitMasaCompat = ofBoolean("Wthit与Masa兼容", true, "在合适的时机不渲染tooltip");
    public static final ConfigBoolean HeartTypeOverride = ofBoolean("生命值样式覆写", false);
    public static final ConfigEnum<HeartType> HeartTypeValue = ofEnum("生命值样式", HeartType.NORMAL);


    // key settings
    private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);


    // fix
    public static final ConfigBoolean FreeCamKeepAutoMoving = ofBoolean("灵魂出窍时允许自动移动", true, "本模组的自动移动, 在灵魂出窍时会默认停止移动");
    public static final ConfigBoolean FreeCamSpectatorFix = ofBoolean("旁观模式灵魂出窍修复", true, "当你附身别的生物, 启动灵魂出窍时相机仍在附身地");
    public static final ConfigBoolean ToolSwitchFix = ofBoolean("工具切换修复", true, "无合适工具时, 不应切换到第一个快捷栏");


    // list
    public static final ConfigStringList TradingTargets = ofStringList("定向交易目标", ImmutableList.of("lapis_lazuli"), "需打开定向自动交易功能");
    public static final ConfigStringList AutoRepeatBlackList = ofStringList("自动复读字符串黑名单", ImmutableList.of(), "关于自动复读:\n首先你需要安装有clientcommands或者clientarguments模组\n在指令中输入dc即可自动补全");
    public static final ConfigStringList AutoThrowWhiteList = ofStringList("自动丢弃白名单", ImmutableList.of());
    public static final ConfigStringList CullEntityList = ofStringList("剔除实体渲染列表", ImmutableList.of(), "见EntityTypes");
    public static final ConfigStringList MuteSoundList = ofStringList("静音音效列表", ImmutableList.of(), "见SoundEvents");
    public static final ConfigStringList CullParticleList = ofStringList("剔除粒子列表", ImmutableList.of(), "见ParticleTypes");


    // key
    public static final ConfigHotkey OpenWindow = ofHotkey("打开设置菜单", "D,C", "打开设置菜单");
    public static final ConfigHotkey ReloadCommandButton = ofHotkey("重载CommandButton", "", "需要有CommandButton模组");
    public static final ConfigHotkey SortItem = ofHotkey("整理物品", "", KeybindSettings.GUI, "按区域进行\n兼容carpet假人不会乱点按钮\n兼容创造模式物品栏");
    public static final ConfigHotkey StoneCutterRecipeView = ofHotkey("展示切石机配方", "A", GUI_RELAXED);
    public static final ConfigHotkey StoreStoneCutterRecipe = ofHotkey("储存切石机配方", "BUTTON_3", GUI_RELAXED_CANCEL);
    public static final ConfigHotkey CutStone = ofHotkey("切石", "LEFT_CONTROL, C", GUI_NO_ORDER);
    public static final ConfigHotkey CutStoneThenThrow = ofHotkey("切石并丢出", "LEFT_CONTROL,LEFT_ALT,C", GUI_NO_ORDER);
    public static final ConfigHotkey MyMassCrafting = ofHotkey("我的喷射合成", "", GUI_NO_ORDER, "作为ItemScroller的替代品\n虽然仍然需要安装它才能用(以便读取配方)\n而且需要较高版本");
    public static final ConfigHotkey ThrowSection = ofHotkey("清空区域", "", KeybindSettings.GUI, "全部丢出");
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


    public static final ConfigHotkey TEST = ofHotkey("测试", "", KeybindSettings.GUI);


    // toggle
    public static final ConfigBooleanHotkeyed AUTO_WALK = ofBooleanHotkeyed("自动前进", false, "LEFT_ALT,UP", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_LEFT = ofBooleanHotkeyed("自动向左", false, "LEFT_ALT,LEFT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_BACK = ofBooleanHotkeyed("自动后退", false, "LEFT_ALT,DOWN", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_RIGHT = ofBooleanHotkeyed("自动向右", false, "LEFT_ALT,RIGHT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed MonitorPortalGeneration = ofBooleanHotkeyed("监听传送门生成", false, "", "限单机游戏");
    public static final ConfigBooleanHotkeyed MonitorThunderWeather = ofBooleanHotkeyed("监听雷暴天气", false, "");
    public static final ConfigBooleanHotkeyed StartStoneCutting = ofBooleanHotkeyed("启动连续切石", false, "", KeybindSettings.INGAME_BOTH);
    public static final ConfigBooleanHotkeyed StartMassCrafting = ofBooleanHotkeyed("启动连续喷射合成", false, "", KeybindSettings.INGAME_BOTH);
    public static final ConfigBooleanHotkeyed AutoGuiQuitting = ofBooleanHotkeyed("自动关闭容器GUI", false, "", KeybindSettings.INGAME_BOTH, "不会关闭容器之外的GUI");
    public static final ConfigBooleanHotkeyed OrientedAutoTrading = ofBooleanHotkeyed("定向自动交易", false, "", "打开交易GUI时自动交易所有在名单上的物品");
    public static final ConfigBooleanHotkeyed LoyalerTrident = ofBooleanHotkeyed("更忠诚的三叉戟", false, "", "发射的忠诚三叉戟能够回到副手");
    public static final ConfigBooleanHotkeyed PathNodesVisibility = ofBooleanHotkeyed("寻路节点可视化", false, "");
    public static final ConfigBooleanHotkeyed PathNodesOnlyNamed = ofBooleanHotkeyed("寻路节点仅限命名生物", false, "");
    public static final ConfigBooleanHotkeyed AutoThrow = ofBooleanHotkeyed("自动丢弃", false, "", "在GUI中不生效");
    public static final ConfigBooleanHotkeyed WorldEditVisibility = ofBooleanHotkeyed("WorldEdit可视化", false, "", "作为WECUI的暂时替代, 仅支持长方体选区, 且渲染需要litematica");
    public static final ConfigBooleanHotkeyed AutoContainerTaker = ofBooleanHotkeyed("自动从容器取出", false, "", "若完全取出, 自动关闭GUI");
    public static final ConfigBooleanHotkeyed AutoContainerClassifier = ofBooleanHotkeyed("自动向容器归类", false, "", "例如: 若容器中有圆石, 则将物品栏的圆石移入");
    public static final ConfigBooleanHotkeyed AutoExtinguisher = ofBooleanHotkeyed("自动灭火", false, "", "不影响灵魂火");
    public static final ConfigBooleanHotkeyed ForceRenderEndGatewayBeam = ofBooleanHotkeyed("强制渲染末地折跃门光柱", false, "", "");


    // yeet
    public static final ConfigBooleanHotkeyed CullSignRendering = ofBooleanHotkeyed("剔除告示牌渲染", false, "");
    public static final ConfigBooleanHotkeyed CullItemFrame = ofBooleanHotkeyed("剔除物品展示框渲染", false, "");
    public static final ConfigBooleanHotkeyed CullItemEntity = ofBooleanHotkeyed("剔除物品实体渲染", false, "");
    public static final ConfigBooleanHotkeyed CullExperienceOrb = ofBooleanHotkeyed("剔除经验球渲染", false, "");
    public static final ConfigBooleanHotkeyed DarknessOverride = ofBooleanHotkeyed("禁用失明和黑暗", false, "");
    public static final ConfigBooleanHotkeyed MuteExplosion = ofBooleanHotkeyed("爆炸静音", false, "", "不包括龙息爆炸");
    public static final ConfigBooleanHotkeyed MuteWither = ofBooleanHotkeyed("凋灵静音", false, "");
    public static final ConfigBooleanHotkeyed MuteEnderman = ofBooleanHotkeyed("末影人静音", false, "");
    public static final ConfigBooleanHotkeyed MuteDispenser = ofBooleanHotkeyed("发射器静音", false, "", "包括投掷器, 仅屏蔽发射失败音效");
    public static final ConfigBooleanHotkeyed MuteMinecart = ofBooleanHotkeyed("矿车静音", false, "");
    public static final ConfigBooleanHotkeyed MuteThunder = ofBooleanHotkeyed("雷声静音", false, "");
    public static final ConfigBooleanHotkeyed MuteGuardian = ofBooleanHotkeyed("守卫者静音", false, "");
    public static final ConfigBooleanHotkeyed MuteAnvil = ofBooleanHotkeyed("铁砧静音", false, "");
    public static final ConfigBooleanHotkeyed CullPoofParticle = ofBooleanHotkeyed("剔除生物死亡粒子", false, "", "即poof, 详见wiki");
    public static final ConfigBooleanHotkeyed BlockBreakingCooldownOverride = ofBooleanHotkeyed("禁用方块挖掘冷却", false, "", "不影响创造模式");
    public static final ConfigBooleanHotkeyed DisableREIWarning = ofBooleanHotkeyed("禁用REI警告", false, "", "至少在18.0.796版本仍然每次进服都在弹窗");
    public static final ConfigBooleanHotkeyed MuteGLDebugInfo = ofBooleanHotkeyed("禁止打印GL调试信息", false, "有时一直在后台打印, 且难以确定错误原因");


    public static final List<IConfigBase> ALL_CONFIGS;


    public static final ImmutableList<IConfigBase> Values;
    public static final ImmutableList<IConfigBase> Fix;
    public static final ImmutableList<IConfigBase> Lists;
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
                SortingContainersLast,
                CachedSorting,
                ItemSortingOrder,
                MassCraftingMode,
                TriggerButtonOffset,
                ProgressResuming,
                AutoRepeatBlackListMode,
                AutoRepeatBlackListReplace,
                AutoRepeatAntiDDos,
                AutoRepeatAntiDDosThreshold,
                WorldEditOverlay,
                InventoryPreviewSupportComparator,
                PinYinSearch,
                CommentSearch,
                FullDebugInfo,
                XRayAutoColor,
                WthitMasaCompat,
                HeartTypeOverride,
                HeartTypeValue
        );
        Fix = ImmutableList.of(
                FreeCamKeepAutoMoving,
                FreeCamSpectatorFix,
                ToolSwitchFix
        );
        Lists = ImmutableList.of(
                TradingTargets,
                AutoRepeatBlackList,
                AutoThrowWhiteList,
                CullEntityList,
                MuteSoundList,
                CullParticleList
        );
        KeyPress = ImmutableList.of(
                OpenWindow,
                ReloadCommandButton,
                SortItem,
                StoneCutterRecipeView,
                StoreStoneCutterRecipe,
                CutStone,
                CutStoneThenThrow,
                MyMassCrafting,
                ThrowSection,
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
                AutoGuiQuitting,
                OrientedAutoTrading,
                LoyalerTrident,
                PathNodesVisibility,
                PathNodesOnlyNamed,
                AutoThrow,
                WorldEditVisibility,
                AutoContainerTaker,
                AutoContainerClassifier,
                AutoExtinguisher,
                ForceRenderEndGatewayBeam
        );
        Yeets = ImmutableList.of(
                CullSignRendering,
                CullItemFrame,
                CullItemEntity,
                CullExperienceOrb,
                DarknessOverride,
                MuteExplosion,
                MuteWither,
                MuteEnderman,
                MuteDispenser,
                MuteMinecart,
                MuteThunder,
                MuteGuardian,
                MuteAnvil,
                CullPoofParticle,
                BlockBreakingCooldownOverride,
                DisableREIWarning,
                MuteGLDebugInfo
        );
        ImmutableList.Builder<IConfigBase> builder = ImmutableList.builder();
        builder.addAll(Values);
        builder.addAll(Fix);
        builder.addAll(Lists);
        builder.addAll(KeyToggle);
        builder.addAll(KeyPress);
        builder.addAll(Yeets);
        ALL_CONFIGS = builder.build();
        INSTANCE.load();
    }
}
