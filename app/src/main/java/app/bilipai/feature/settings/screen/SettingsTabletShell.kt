package app.bilipai.feature.settings.screen

import app.bilipai.feature.settings.SettingsFunctionIcon
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.ui.AppContentColors

import android.os.Build
import app.bilipai.core.ui.globalWallpaperAwareBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import app.bilipai.core.ui.components.AppLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import app.bilipai.core.ui.components.AppIcon
import app.bilipai.core.ui.components.AppIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import app.bilipai.core.ui.components.AppNavigationDrawerItem
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.ui.components.rememberAdaptivePreferenceIconContentColor
import app.bilipai.core.ui.components.rememberAdaptivePreferenceIconContainerColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.bilipai.R
import app.bilipai.core.ui.AppShapes
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.AppTopBar
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.LocalAppThemeConfig
import app.bilipai.core.ui.performance.isLowBlurBudgetForced
import app.bilipai.core.ui.blur.BlurSurfaceType
import app.bilipai.core.ui.blur.hazeSourceCompat
import app.bilipai.core.ui.blur.recoverableBlurEnabled
import app.bilipai.core.ui.blur.rememberRecoverableHazeState
import app.bilipai.core.ui.blur.shouldAllowRenderEffectBackedHazeEffect
import app.bilipai.core.ui.blur.unifiedBlur
import app.bilipai.core.ui.rememberAppBackIcon
import app.bilipai.feature.home.components.BiliPaiImmersiveTopBar
import app.bilipai.feature.home.components.shouldUseBiliPaiProgressiveTopBlur
import app.bilipai.core.ui.blur.rememberChromeBackdropSource
import app.bilipai.core.util.AppFoldPosture
import app.bilipai.core.util.LocalAppWindowAdaptiveInfo
import app.bilipai.core.util.LocalWindowSizeClass
import app.bilipai.feature.settings.SettingsHomeSearchEntry
import app.bilipai.feature.settings.SettingsRootCategory
import app.bilipai.feature.settings.canonicalSettingsRootCategory
import app.bilipai.feature.settings.rememberSettingsEntryVisual
import app.bilipai.feature.settings.rememberSettingsSiblingIconTints
import app.bilipai.feature.settings.resolveSettingsRootCategoryOrder
import app.bilipai.feature.settings.resolveSettingsTabletLayoutPolicy
import app.bilipai.feature.settings.resolveSettingsVisualSpec
import app.bilipai.feature.settings.subtitleResource
import app.bilipai.feature.settings.titleResource
import app.bilipai.feature.settings.shouldRenderSettingsTabletDetailPane
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsTabletShell(
    selectedCategory: SettingsRootCategory?,
    onCategoryClick: (SettingsRootCategory) -> Unit,
    onBack: () -> Unit,
    onSearchOpen: () -> Unit,
    modifier: Modifier = Modifier,
    isSearchActive: Boolean = false,
    rightPane: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val layoutPolicy = remember(configuration.screenWidthDp) {
        resolveSettingsTabletLayoutPolicy(widthDp = configuration.screenWidthDp)
    }
    val categories = remember { resolveSettingsRootCategoryOrder() }
    val isDetailActive = shouldRenderSettingsTabletDetailPane(
        selectedCategory = selectedCategory,
        isSearchActive = isSearchActive,
    )
    val categoryIconTints = rememberSettingsSiblingIconTints(categories.size)
    val useThreePaneLayout =
        LocalWindowSizeClass.current.shouldUseThreePaneLayout && selectedCategory != null
    val emptyDetailPane: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .globalWallpaperAwareBackground(AppSurfaceTokens.groupedListContainer())
                .padding(layoutPolicy.detailPanePaddingDp.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppText(
                    text = "选择设置分类",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                AppText(
                    text = "从左侧选择一个分类以查看和修改设置",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppContentColors.secondaryText(),
                )
            }
        }
    }
    val detailPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .globalWallpaperAwareBackground(AppSurfaceTokens.groupedListContainer())
                .padding(horizontal = layoutPolicy.detailPanePaddingDp.dp),
        ) {
            rightPane()
        }
    }
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive = calculatePaneScaffoldDirective(windowAdaptiveInfo)
    // 铰链/窄窗口导致只能单分区时退化为按导航态单栏切换；正常平板态永远双栏可见。
    // Tabletop 姿态下官方 directive 不会减少水平分区，需按旧策略显式退回单栏避免跨铰链布局。
    val singlePaneOnly = directive.maxHorizontalPartitions <= 1 ||
        LocalAppWindowAdaptiveInfo.current.posture == AppFoldPosture.Tabletop
    val listPaneContent: @Composable ThreePaneScaffoldPaneScope.() -> Unit = {
        Column(
            modifier = Modifier
                .preferredWidth(layoutPolicy.primaryRatio)
                .fillMaxHeight()
                .globalWallpaperAwareBackground(AppSurfaceTokens.groupedListContainer()),
        ) {
                val config = LocalAppThemeConfig.current
                val progressive = shouldUseBiliPaiProgressiveTopBlur(
                    enabled = config.progressiveTopBlurEnabled && !config.headerBlurEnabled,
                    hasBackdrop = true,
                ) && !isLowBlurBudgetForced()
                val fadeActive = config.progressiveTopFadeEnabled && !config.headerBlurEnabled
                val tabletChromeSource = if (progressive) rememberChromeBackdropSource(
                    recordingBackground = AppSurfaceTokens.groupedListContainer(),
                ) else null
                val tabletChromeBackdrop = tabletChromeSource?.takeIf { it.isReady }?.backdrop
                val tabletHazeState = if (
                    config.headerBlurEnabled && !progressive &&
                        shouldAllowRenderEffectBackedHazeEffect(Build.VERSION.SDK_INT)
                ) rememberRecoverableHazeState() else null
                val tabletHazeReady = tabletHazeState?.let { recoverableBlurEnabled(it) } == true
                BiliPaiImmersiveTopBar(
                    // 双栏标题与搜索框上下排列；顶部特效不得外绘到下方独立搜索胶囊。
                    modifier = if (progressive) Modifier.clipToBounds() else Modifier,
                    backdrop = tabletChromeBackdrop,
                    enabled = progressive,
                    headerBlurActive = tabletHazeReady,
                    surfaceColor = AppSurfaceTokens.groupedListContainer(),
                    fadeEnabled = fadeActive,
                ) {
                    AppTopBar(
                        title = stringResource(R.string.settings_title),
                        modifier = if (tabletHazeReady) {
                            Modifier
                                .unifiedBlur(
                                    hazeState = requireNotNull(tabletHazeState),
                                    surfaceType = BlurSurfaceType.HEADER,
                                )
                                .background(
                                    AppSurfaceTokens.groupedListContainer()
                                        .copy(alpha = AppSurfaceTokens.FrostedScrimAlpha)
                                )
                        } else Modifier,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (progressive || fadeActive || tabletHazeReady) {
                                androidx.compose.ui.graphics.Color.Transparent
                            } else {
                                AppSurfaceTokens.groupedListContainer()
                            },
                            scrolledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                        navigationIcon = {
                            AppIconButton(onClick = onBack) {
                                AppIcon(
                                    imageVector = rememberAppBackIcon(),
                                    contentDescription = stringResource(R.string.common_back),
                                )
                            }
                        },
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .then(tabletChromeSource?.modifier ?: Modifier)
                        .then(if (tabletHazeReady) Modifier.hazeSourceCompat(requireNotNull(tabletHazeState)) else Modifier)
                        .padding(horizontal = layoutPolicy.masterPanePaddingDp.dp),
                ) {
                    SettingsHomeSearchEntry(onClick = onSearchOpen)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    ) {
                        itemsIndexed(categories) { index, category ->
                            val visual = rememberSettingsEntryVisual(category.searchTarget)
                            val effectiveIconTint = rememberAdaptivePreferenceIconContainerColor(
                                categoryIconTints[index]
                            )
                            val iconContentColor = rememberAdaptivePreferenceIconContentColor(effectiveIconTint)
                            val selected = selectedCategory?.let(::canonicalSettingsRootCategory) == category
                            AppNavigationDrawerItem(
                                label = {
                                    Column {
                                        AppText(stringResource(category.titleResource()), fontWeight = FontWeight.Medium)
                                        AppText(
                                            text = stringResource(category.subtitleResource()),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppContentColors.secondaryText(),
                                            maxLines = 2,
                                        )
                                    }
                                },
                                selected = selected,
                                onClick = {
                                    if (!selected) {
                                        onCategoryClick(category)
                                    }
                                },
                                icon = {
                                    if (LocalAppUiStyle.current == AppUiStyle.MIUIX || app.bilipai.core.ui.components.useColorfulPreferenceIcons()) {
                                        SettingsFunctionIcon(
                                            title = category.title,
                                            imageVector = visual.icon,
                                            painter = visual.iconResId?.let { androidx.compose.ui.res.painterResource(it) },
                                        )
                                    } else Box(
                                        modifier = Modifier
                                            .size(resolveSettingsVisualSpec().categoryIconBubbleSize)
                                            .clip(AppShapes.container(ContainerLevel.Field))
                                            .background(effectiveIconTint),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (visual.icon != null) {
                                            AppIcon(
                                                imageVector = visual.icon,
                                                contentDescription = null,
                                                tint = iconContentColor,
                                                modifier = Modifier.size(visual.iconSizeDp.dp),
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    val infoPaneContent: @Composable ThreePaneScaffoldPaneScope.() -> Unit = {
        Column(
            modifier = Modifier
                .preferredWidth(0.35f)
                .fillMaxSize()
                .globalWallpaperAwareBackground(AppSurfaceTokens.groupedListContainer())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppText(
                text = selectedCategory?.let { stringResource(it.titleResource()) }
                    ?: stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            AppText(
                text = selectedCategory?.let { stringResource(it.subtitleResource()) }
                    ?: stringResource(R.string.settings_category_select_hint),
                style = MaterialTheme.typography.bodyLarge,
                color = AppContentColors.secondaryText(),
            )
        }
    }
    val detailSlotContent: @Composable ThreePaneScaffoldPaneScope.() -> Unit = {
        if (!isDetailActive) emptyDetailPane() else detailPane()
    }
    // 官方脚手架的槽位映射：list=listPane，detail=primary 槽，extra=tertiary 槽。
    // 三栏（超宽 + 已选分类）时 detail 槽放分类信息列，extra 槽放设置正文。
    val scaffoldValue = when {
        singlePaneOnly && isDetailActive -> ThreePaneScaffoldValue(
            primary = PaneAdaptedValue.Expanded,
            secondary = PaneAdaptedValue.Hidden,
            tertiary = PaneAdaptedValue.Hidden,
        )
        singlePaneOnly -> ThreePaneScaffoldValue(
            primary = PaneAdaptedValue.Hidden,
            secondary = PaneAdaptedValue.Expanded,
            tertiary = PaneAdaptedValue.Hidden,
        )
        useThreePaneLayout && !isSearchActive -> ThreePaneScaffoldValue(
            primary = PaneAdaptedValue.Expanded,
            secondary = PaneAdaptedValue.Expanded,
            tertiary = PaneAdaptedValue.Expanded,
        )
        else -> ThreePaneScaffoldValue(
            primary = PaneAdaptedValue.Expanded,
            secondary = PaneAdaptedValue.Expanded,
            tertiary = PaneAdaptedValue.Hidden,
        )
    }
    ListDetailPaneScaffold(
        modifier = modifier.fillMaxSize(),
        directive = directive,
        value = scaffoldValue,
        listPane = listPaneContent,
        detailPane = if (useThreePaneLayout && !isSearchActive) infoPaneContent else detailSlotContent,
        extraPane = if (useThreePaneLayout && !isSearchActive) detailPane else null,
    )
}
