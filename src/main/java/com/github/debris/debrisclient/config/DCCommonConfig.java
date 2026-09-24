package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.api.MatchType;
import com.github.debris.debrisclient.config.api.RequiresMod;
import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.feat.HeartType;
import com.github.debris.debrisclient.gui.MainConfigScreen;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IHotkeyTogglable;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.data.ModInfo;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.List;

import static com.github.debris.debrisclient.config.ConfigFactory.*;

public class DCCommonConfig extends ConfigHandlerImpl {
    private static final DCCommonConfig INSTANCE;
    public static final Identifier ID = Identifier.fromNamespaceAndPath(DebrisClient.MOD_ID, "main");
    public static final ModInfo MOD_INFO = new ModInfo(ID.toString(), DebrisClient.MOD_NAME + " Main", () -> MainConfigScreen.getInstance(null));


    // value
    public static final ConfigInteger TriggerButtonOffset = ofInteger("触发按钮的坐标偏移", 42, -100, 100, true, "自动对齐可能有问题");
    public static final ConfigInteger AutoRepeatAntiDDos = ofInteger("自动复读防刷屏", Integer.MAX_VALUE, 1, Integer.MAX_VALUE, false, "1秒内同一条消息被发送次数超过阈值时, 将取消之后的发送");
    public static final ConfigBoolean FullDebugInfo = ofBoolean("完整调试权限", false);
    public static final ConfigEnum<HeartType> HeartTypeOverride = ofEnum("生命值样式覆写", HeartType.NONE);
    public static final ConfigBoolean ExtraTooltip = ofBoolean("额外物品提示", false, "对于非满级附魔,标注其最高等级\n铁砧惩罚,在铁砧操作时所需等级\n附魔价值,将其全部魔咒转移时所需等级");
    public static final ConfigInteger InteractContainerPeriod = ofInteger("交互容器间隔", 3, 0, 100, true, "按刻计\n高延迟服务器内应调高此项");
    public static final ConfigBoolean RetroDefaultSkin = ofBoolean("怀旧默认皮肤", false, "仅Steve, Alex");
    public static final ConfigBoolean ChunkBorderRenderNotOnTop = ofBoolean("区块边界渲染不再置顶", false, "1.21.11+子区块边界会透视");
    public static final ConfigString SpawnBotPrefix = ofString("召唤假人前缀", "bot_");


    // integration
    public static final ConfigBoolean ProgressResuming = ofBoolean("进度恢复", true, "打开配置页面时, 能跳转上次进度\n对MaLiLib驱动的模组和CommandButton有效");
    public static final ConfigBoolean PinYinSearch = ofBoolean("拼音搜索", false, "需要Rei, 支持由MaLiLib驱动的模组, 创造模式物品栏, 配方书");
    public static final ConfigBoolean CommentSearch = ofBoolean("注释搜索", false, "对MaLiLib驱动的模组有效");
    public static final ConfigBoolean GlobalConfigEnhance = ofBoolean("全局配置加强", false, "将本模组的配置加强应用到所有masa模组,包含以下功能:\n为热键添加触发按钮\n为枚举列表提供预览");
    public static final ConfigBoolean ScrollerEnhance = ofBoolean("滑动条改进", true, "masa驱动\n允许点击白块之外拖动");
    @RequiresMod(ModReference.Tweakeroo)
    public static final ConfigBoolean FreeCamKeepAutoMoving = ofBoolean("灵魂出窍时允许自动移动", true, "本模组的自动移动, 在灵魂出窍时会默认停止移动");
    @RequiresMod(ModReference.Tweakeroo)
    public static final ConfigBoolean FreeCamSpectatorFix = ofBoolean("旁观模式灵魂出窍修复", true, "当你附身别的生物, 启动灵魂出窍时相机仍在附身地");
    @RequiresMod(ModReference.Tweakeroo)
    public static final ConfigBoolean RetroFreeCam = ofBoolean("怀旧灵魂出窍", false, "适用于tweakeroo0.24.1");
    @RequiresMod(ModReference.Tweakeroo)
    public static final ConfigBoolean ToolSwitchFix = ofBoolean("工具切换修复", true, "无合适工具时, 不应轻易切换");
    @RequiresMod(value = {ModReference.WorldEdit, ModReference.Litematica}, matchType = MatchType.ALL)
    public static final ConfigBoolean WorldEditVisibility = ofBoolean("WorldEdit可视化", false, "作为WECUI的暂时替代, 仅支持长方体选区, 且渲染需要litematica");
    @RequiresMod(value = {ModReference.WorldEdit, ModReference.Litematica}, matchType = MatchType.ALL)
    public static final ConfigColor WorldEditOverlay = ofColor("WorldEdit滤镜", "#30FFFF00", "在WE选区渲染后再加上, 以区分litematica的选区");
    @RequiresMod(value = {ModReference.MiniHud, ModReference.MagicLibMCApi})
    public static final ConfigBoolean InventoryPreviewOnComparator = ofBoolean("物品栏预览于比较器", true);
    @RequiresMod(ModReference.XRay)
    public static final ConfigBoolean XRayAutoColor = ofBoolean("XRay自动取色", true);
    @RequiresMod(value = {ModReference.Jade, ModReference.Wthit}, matchType = MatchType.ANY)
    public static final ConfigBoolean WailaMasaCompat = ofBoolean("Waila与Masa兼容", true, "在合适的时机不渲染tooltip\njade, wthit");
    @RequiresMod(ModReference.REI)
    public static final ConfigBoolean DisableREIWarning = ofBoolean("禁用REI警告", false, "至少在18.0.796版本仍然每次进服都在弹窗");
    @RequiresMod(ModReference.MiniHud)
    public static final ConfigBoolean AlwaysAssumeServux = ofBoolean("总是假定Servux", false, "在0.41.1中重新进入服务器会使配置关闭");


    // list
    public static final ConfigStringList AutoRepeatPlayerList = ofStringList("自动复读玩家列表");
    public static final ConfigStringList AutoRepeatBlackList = ofStringList("自动复读字符串黑名单", ImmutableList.of(), "可用样式如下:\n直接取消复读,如\"debris\"\n箭头->表示替换,如\"debris->spirit\"");
    public static final ConfigStringList CullBlockEntityList = ofStringList("剔除方块实体列表");
    public static final ConfigStringList CullEntityList = ofStringList("剔除实体渲染列表");
    public static final ConfigStringList CullParticleList = ofStringList("剔除粒子列表");
    public static final ConfigStringList MuteSoundList = ofStringList("静音音效列表");
    public static final ConfigStringList HighlightEntityList = ofStringList("高亮实体列表");


    // key setting
    private static final KeybindSettings ANY = KeybindSettings.create(KeybindSettings.Context.ANY, KeyAction.PRESS, false, true, false, true);


    // key
    public static final ConfigHotkey OpenConfigScreen = ofHotkey("打开设置菜单", "D,C", "打开设置菜单");
    public static final ConfigHotkey OpenInventoryConfigScreen = ofHotkey("打开物品栏设置", "D,I", "可以直接点击触发");
    public static final ConfigHotkey OpenUniversalSearch = ofHotkey("打开全局搜索", "", "masa驱动");
    public static final ConfigHotkey KickBot = ofHotkey("踢出假人", "", KeybindSettings.PRESS_ALLOWEXTRA, "按住时踢出准心所指假人\n支持灵魂出窍");
    public static final ConfigHotkey RestoreKicking = ofHotkey("假人复原", "", "召回误踢的假人");
    public static final ConfigHotkey SuggestBotSpawnCommand = ofHotkey("假人召唤指令", "", "在聊天栏中建议当前位置");
    public static final ConfigHotkey ResendLastChat = ofHotkey("重发上一条消息", "", "相当于按UP键");
    public static final ConfigHotkey RepeatNewestChat = ofHotkey("消息复读", "", "复读聊天栏中最新消息");
    public static final ConfigHotkey AlignWithEnderEye = ofHotkey("对齐末影之眼");
    public static final ConfigHotkey TakeOff = ofHotkey("起飞", "", KeybindSettings.PRESS_ALLOWEXTRA, "使用鞘翅和烟花火箭起飞");
    public static final ConfigHotkey OpenSelectionContainers = ofHotkey("打开选区内容器", "", ANY, "记录列表, 之后逐个打开");
    public static final ConfigHotkey InteractSelectionEntities = ofHotkey("交互选区内实体", "", ANY, "记录列表, 之后逐个交互");


    public static final ConfigHotkey TEST = ofHotkey("测试", "", KeybindSettings.GUI);


    // toggle
    public static final ConfigBooleanHotkeyed AUTO_FORWARD = ofBooleanHotkeyed("自动前进", false, "LEFT_ALT,UP", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_LEFT = ofBooleanHotkeyed("自动向左", false, "LEFT_ALT,LEFT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_BACK = ofBooleanHotkeyed("自动后退", false, "LEFT_ALT,DOWN", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_RIGHT = ofBooleanHotkeyed("自动向右", false, "LEFT_ALT,RIGHT", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_JUMP = ofBooleanHotkeyed("自动跳跃", false, "LEFT_ALT,SPACE", "可用于走路，划船");
    public static final ConfigBooleanHotkeyed AUTO_SQUAT = ofBooleanHotkeyed("自动蹲起", false, "LEFT_ALT,RIGHT_SHIFT", "");
    public static final ConfigBooleanHotkeyed AUTO_ROTATE = ofBooleanHotkeyed("自动旋转", false, "LEFT_ALT,ENTER");
    public static final ConfigBooleanHotkeyed LoyalerTrident = ofBooleanHotkeyed("更忠诚的三叉戟", false, "", "发射的忠诚三叉戟能够回到副手");
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
    public static final ImmutableList<IConfigBase> Integration;
    public static final ImmutableList<IConfigBase> Lists;
    public static final ImmutableList<ConfigHotkey> KeyPress;
    public static final ImmutableList<IHotkeyTogglable> KeyToggle;
    public static final ImmutableList<IHotkeyTogglable> Yeets;
    public static final ImmutableList<IHotkeyTogglable> Highlights;

    public DCCommonConfig(Path path, List<? extends IConfigBase> configs) {
        super(path, configs);
    }

    public static DCCommonConfig getInstance() {
        return INSTANCE;
    }

    static {
        Values = ImmutableList.of(
                TriggerButtonOffset,
                AutoRepeatAntiDDos,
                FullDebugInfo,
                HeartTypeOverride,
                ExtraTooltip,
                InteractContainerPeriod,
                RetroDefaultSkin,
                ChunkBorderRenderNotOnTop,
                SpawnBotPrefix
        );
        Integration = ImmutableList.of(
                ProgressResuming,
                PinYinSearch,
                CommentSearch,
                GlobalConfigEnhance,
                ScrollerEnhance,
                FreeCamKeepAutoMoving,
                FreeCamSpectatorFix,
                RetroFreeCam,
                ToolSwitchFix,
                WorldEditVisibility,
                WorldEditOverlay,
                InventoryPreviewOnComparator,
                XRayAutoColor,
                WailaMasaCompat,
                DisableREIWarning,
                AlwaysAssumeServux
        );
        Lists = ImmutableList.of(
                AutoRepeatPlayerList,
                AutoRepeatBlackList,
                CullBlockEntityList,
                CullEntityList,
                CullParticleList,
                MuteSoundList,
                HighlightEntityList
        );
        KeyPress = ImmutableList.of(
                OpenConfigScreen,
                OpenInventoryConfigScreen,
                OpenUniversalSearch,
                KickBot,
                RestoreKicking,
                SuggestBotSpawnCommand,
                ResendLastChat,
                RepeatNewestChat,
                AlignWithEnderEye,
                TakeOff,
                OpenSelectionContainers,
                InteractSelectionEntities,
                TEST
        );
        KeyToggle = ImmutableList.of(
                AUTO_FORWARD,
                AUTO_LEFT,
                AUTO_RIGHT,
                AUTO_BACK,
                AUTO_JUMP,
                AUTO_SQUAT,
                AUTO_ROTATE,
                LoyalerTrident,
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
        builder.addAll(Integration);
        builder.addAll(Lists);
        builder.addAll(KeyToggle);
        builder.addAll(KeyPress);
        builder.addAll(Yeets);
        builder.addAll(Highlights);
        ALL_CONFIGS = builder.build();
        INSTANCE = new DCCommonConfig(DebrisClient.CONFIG_DIR.resolve("config_common.json"), ALL_CONFIGS);
        INSTANCE.load();
    }
}
