package app.bilipai.feature.search

import app.bilipai.feature.home.resolveIsVerticalForNavigation
import app.bilipai.feature.home.effectiveGridColumnCount
import app.bilipai.feature.home.effectiveGridColumnCountCompact
import app.bilipai.feature.home.gridColumnPinchEnabled
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.components.videoListItemModifier
import app.bilipai.core.ui.components.AnimatedVideoListItem
import app.bilipai.core.ui.components.AppText
import app.bilipai.core.util.BilibiliNavigationTarget
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import app.bilipai.feature.home.rememberFeedGridColumnPreference
import app.bilipai.feature.home.homeFeedPinchZoom
import app.bilipai.feature.home.resolveHomeFeedPinchColumnBounds
import app.bilipai.feature.home.GridPinchColumnHudPill
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import app.bilipai.core.ui.components.AppLazyColumn as LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import app.bilipai.core.ui.components.AppLazyVerticalStaggeredGrid as LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Job
import app.bilipai.R
import app.bilipai.core.ui.AppShapes
import app.bilipai.feature.live.rememberLiveCardContainerTransition
import app.bilipai.core.ui.ContainerLevel
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.adaptive.MotionTier
import app.bilipai.core.ui.skeleton.ContentMediaListSkeleton
import app.bilipai.core.ui.skeleton.ContentVideoGridSkeletonFixedColumns
import app.bilipai.feature.home.resolveHomeCoverRequestSpec
import app.bilipai.feature.home.components.cards.ElegantVideoCard  //  使用首页卡片
import app.bilipai.feature.home.resolveHomeFeedCardLayout
import app.bilipai.core.store.HomeDurationStyle
import app.bilipai.core.store.HomeFeedCardStyle
import app.bilipai.core.store.HomeSettings
import app.bilipai.core.util.responsiveContentWidth
import app.bilipai.core.ui.blur.hazeSourceCompat
import app.bilipai.core.util.LocalWindowSizeClass
import app.bilipai.data.model.response.SearchType
import kotlinx.coroutines.launch
import app.bilipai.core.ui.AdaptiveLoadingIndicator
import app.bilipai.feature.home.HomeFeedCardLayout

internal data class SearchResultPageAppearance(
    val singleColumn: Boolean,
    val searchLayoutPolicy: SearchLayoutPolicy,
    val cardAnimationEnabled: Boolean,
    val homeDurationStyle: HomeDurationStyle,
    val compactVideoStatsOnCover: Boolean,
    val cardMotionTier: MotionTier,
    val videoCardAppearance: SearchVideoCardAppearance,
    val genericResultCardAppearance: SearchResultCardAppearance,
    val showOnlineCount: Boolean,
    val homeFeedCardStyle: HomeFeedCardStyle,
    val homeSettings: HomeSettings,
    val videoGridColumns: Int,
    val cardLayout: HomeFeedCardLayout,
    val searchHazeEnabled: Boolean,
    val effectiveCardTransitionEnabled: Boolean,
    val isReturningFromVideoDetail: Boolean,
    val isQuickReturningFromVideoDetail: Boolean,
)

/** 单个结果页只渲染自身数据；筛选、输入和导航由页面入口协调。 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun SearchResultPageContent(
    pageResultState: SearchResultPageUiState,
    targetSearchType: SearchType,
    activeSearchType: SearchType,
    searchSessionId: Long,
    isPageVisible: () -> Boolean,
    resultGridState: androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState,
    resultListState: LazyListState,
    scrollToTopSearchType: SearchType?,
    scrollToTopRequestId: Int,
    appearance: SearchResultPageAppearance,
    hazeState: dev.chrisbanes.haze.HazeState,
    resultTopPadding: androidx.compose.ui.unit.Dp,
    resultBottomPadding: androidx.compose.ui.unit.Dp,
    onLoadMore: () -> Unit,
    onRetrySearch: (String) -> Unit,
    onWatchLater: (String, Long) -> Unit,
    onNavigateSearchTarget: (BilibiliNavigationTarget) -> Boolean,
    onVideoClick: (String, Long, String, Boolean) -> Unit,
    onWebClick: (String, String) -> Unit,
    onUpClick: (Long) -> Unit,
    onBangumiClick: (Long) -> Unit,
    onCheeseClick: ((Long, Long) -> Unit)?,
    onLiveClick: (Long, String, String) -> Unit,
    onTopicClick: (Long) -> Unit,
    onArticleClick: (Long, String) -> Unit,
    onUpResultClick: ((Long, String) -> Unit)?,
    onBangumiResultClick: ((Long, String) -> Unit)?,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val singleColumn = appearance.singleColumn
    val searchLayoutPolicy = appearance.searchLayoutPolicy
    val cardAnimationEnabled = appearance.cardAnimationEnabled
    val homeDurationStyle = appearance.homeDurationStyle
    val compactVideoStatsOnCover = appearance.compactVideoStatsOnCover
    val cardMotionTier = appearance.cardMotionTier
    val videoCardAppearance = appearance.videoCardAppearance
    val genericResultCardAppearance = appearance.genericResultCardAppearance
    val showOnlineCount = appearance.showOnlineCount
    val homeFeedCardStyle = appearance.homeFeedCardStyle
    val homeSettings = appearance.homeSettings
    val videoGridColumns = appearance.videoGridColumns
    val cardLayout = appearance.cardLayout
    val searchHazeEnabled = appearance.searchHazeEnabled
    val effectiveCardTransitionEnabled = appearance.effectiveCardTransitionEnabled
    val isReturningFromVideoDetail = appearance.isReturningFromVideoDetail
    val isQuickReturningFromVideoDetail = appearance.isQuickReturningFromVideoDetail

    val pageEmptyStateCopy = remember(pageResultState.emptyStateReason, targetSearchType) {
        if (pageResultState.emptyStateReason == SearchEmptyStateReason.NONE) {
            null
        } else {
            resolveSearchEmptyStateCopy(
                reason = pageResultState.emptyStateReason,
                searchType = targetSearchType
            )
        }
    }
    val isEmptyVideoPage = targetSearchType == SearchType.VIDEO &&
        pageResultState.totalCount == 0
    val pageError = pageResultState.error
        ?: pageResultState.loadMoreError.takeIf { isEmptyVideoPage }
    val isFillingEmptyVideoPage = isEmptyVideoPage &&
        pageResultState.hasMoreResults && pageError == null
    val pagePresentation = remember(
        pageResultState.totalCount,
        pageResultState.isSearching,
        pageError,
        pageResultState.emptyStateReason,
        pageResultState.isLoadingMore,
        pageResultState.loadMoreError,
        pageResultState.hasMoreResults,
        isFillingEmptyVideoPage
    ) {
        resolveSearchResultPresentation(
            itemCount = pageResultState.totalCount,
            isSearching = pageResultState.isSearching || isFillingEmptyVideoPage,
            error = pageError,
            emptyStateReason = pageResultState.emptyStateReason,
            isLoadingMore = pageResultState.isLoadingMore,
            loadMoreError = pageResultState.loadMoreError,
            hasMoreResults = pageResultState.hasMoreResults
        )
    }
    val pageGridState = rememberSaveable(
        pageResultState.query,
        targetSearchType.value,
        saver = app.bilipai.core.ui.CompatibleStaggeredGridStateSaver
    ) {
        app.bilipai.core.ui.createCompatibleStaggeredGridState()
    }
    val pageListState = rememberSaveable(
        pageResultState.query,
        targetSearchType.value,
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }
    val activePageGridState = if (targetSearchType == activeSearchType) {
        resultGridState
    } else {
        pageGridState
    }
    val activePageListState = if (targetSearchType == activeSearchType) {
        resultListState
    } else {
        pageListState
    }
    // Observe the viewport, not a specific card's composition. This also
    // continues after a page is entirely removed by local search filters.
    val latestVideoPage by rememberUpdatedState(pageResultState)
    if ((targetSearchType == SearchType.VIDEO || targetSearchType == SearchType.ALL) &&
        targetSearchType == activeSearchType
    ) {
        LaunchedEffect(activePageGridState, searchSessionId) {
            snapshotFlow {
                val current = latestVideoPage
                val itemCount = activePageGridState.layoutInfo.totalItemsCount
                current.currentPage to shouldLoadMoreSearchVideos(
                    itemCount = itemCount,
                    lastVisibleItemIndex = activePageGridState.layoutInfo
                        .visibleItemsInfo.maxOfOrNull { it.index } ?: -1,
                    hasMoreResults = current.hasMoreResults,
                    isSearching = current.isSearching,
                    isLoadingMore = current.isLoadingMore,
                    hasError = current.error != null || current.loadMoreError != null,
                )
            }.collect { (_, shouldLoad) ->
                if (shouldLoad) onLoadMore()
            }
        }
    }
    LaunchedEffect(scrollToTopRequestId, scrollToTopSearchType, targetSearchType) {
        if (scrollToTopSearchType == targetSearchType && scrollToTopRequestId > 0) {
            if (targetSearchType == SearchType.VIDEO || targetSearchType == SearchType.ALL) {
                activePageGridState.animateScrollToItem(0)
            } else {
                activePageListState.animateScrollToItem(0)
            }
        }
    }
    if (pagePresentation.body == SearchResultBodyMode.LOADING) {
        // 结果形态已知：用骨架占位，不用主题 Loading 动画。
        when (targetSearchType) {
            SearchType.ALL, SearchType.VIDEO -> {
                val skeletonModifier = Modifier
                    .then(
                        if (videoGridColumns == 1) {
                            Modifier.responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxSize()
                ContentVideoGridSkeletonFixedColumns(
                    columns = videoGridColumns,
                    coverAspectRatio = cardLayout.coverAspectRatio,
                    contentPadding = PaddingValues(
                        top = resultTopPadding,
                        bottom = resultBottomPadding,
                        start = cardLayout.outerPaddingDp.dp,
                        end = cardLayout.outerPaddingDp.dp,
                    ),
                    spacing = cardLayout.itemSpacingDp.dp,
                    modifier = skeletonModifier,
                )
            }
            SearchType.UP, SearchType.LIVE_USER -> ContentMediaListSkeleton(
                useUserRow = true,
                contentPadding = PaddingValues(
                    top = resultTopPadding,
                    bottom = resultBottomPadding,
                ),
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize(),
            )
            else -> ContentMediaListSkeleton(
                useUserRow = false,
                contentPadding = PaddingValues(
                    top = resultTopPadding,
                    bottom = resultBottomPadding,
                ),
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize(),
            )
        }
    } else if (pagePresentation.body == SearchResultBodyMode.ERROR) {
        SearchNativeMessageState(
            title = stringResource(R.string.search_failed),
            message = pageError,
            actionLabel = stringResource(R.string.search_retry),
            onAction = {
                if (isEmptyVideoPage && pageResultState.loadMoreError != null) {
                    onLoadMore()
                } else {
                    onRetrySearch(pageResultState.query)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = resultTopPadding, bottom = resultBottomPadding),
            animation = app.bilipai.core.ui.MaidAnimation.RETRY,
            isVisible = isPageVisible()
        )
    } else if (pagePresentation.body == SearchResultBodyMode.EMPTY) {
        val copy = pageEmptyStateCopy
        if (copy != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = resultTopPadding, bottom = resultBottomPadding),
                contentAlignment = Alignment.Center
            ) {
                app.bilipai.core.ui.EmptyState(
                    message = copy.title,
                    subtitle = copy.subtitle,
                    animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                    actionText = stringResource(R.string.search_again),
                    onAction = { onRetrySearch(pageResultState.query) },
                    enableEasterEgg = false,
                    isVisible = isPageVisible()
                )
            }
        }
    } else {
    when (targetSearchType) {
        // 综合与视频共用卡片交互；材质差异仍由原卡片的玻璃开关隔离。
        SearchType.ALL, SearchType.VIDEO -> {
            // Size cover requests against the actual result pane, including split windows.
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val actualGridColumns = resolveSearchVideoGridColumns(
                    singleColumn = singleColumn,
                    contentWidthDp = maxWidth.value.toInt(),
                    fixedColumnCount = app.bilipai.feature.home.resolveHomeFeedStoredColumnCount(
                        widthSizeClass = windowSizeClass.widthSizeClass,
                        compactColumnCount = homeSettings.effectiveGridColumnCountCompact,
                        defaultColumnCount = homeSettings.effectiveGridColumnCount,
                    ),
                    cardWidthPreset = homeSettings.homeFeedCardWidthPreset,
                    widthSizeClass = windowSizeClass.widthSizeClass
                )
                val gridColumnPreference = rememberFeedGridColumnPreference(
                    settings = homeSettings,
                    widthSizeClass = windowSizeClass.widthSizeClass,
                    defaultColumns = actualGridColumns,
                    bounds = if (singleColumn) 1..1 else resolveHomeFeedPinchColumnBounds(
                        windowSizeClass.widthSizeClass, maxWidth.value.toInt(),
                    ),
                )
                var isPinchPillVisible by remember { mutableStateOf(false) }
                var pinchPillDismissJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
                val haptic = app.bilipai.core.ui.rememberAppHapticFeedback(app.bilipai.core.ui.AppHapticScene.MENUS)
                val requestedSearchGridColumns = gridColumnPreference.columns
                val effectiveSearchGridColumns = if (
                    app.bilipai.core.util.LocalAppWindowAdaptiveInfo.current.shouldAvoidHinge
                ) {
                    app.bilipai.core.ui.adaptive.resolveHingeSafeFeedColumns(
                        requestedSearchGridColumns, maxWidth.value,
                        homeSettings.homeFeedCardWidthPreset.minCardWidthDp ?: 180,
                    )
                } else requestedSearchGridColumns
                val pinchColumnBounds = remember(windowSizeClass.widthSizeClass, maxWidth) {
                    resolveHomeFeedPinchColumnBounds(
                        widthSizeClass = windowSizeClass.widthSizeClass,
                        contentWidthDp = maxWidth.value.toInt(),
                    )
                }
                val searchGridCardLayout = remember(
                    homeFeedCardStyle,
                    effectiveSearchGridColumns,
                    windowSizeClass.widthSizeClass,
                ) {
                    resolveHomeFeedCardLayout(
                        style = homeFeedCardStyle,
                        gridColumns = effectiveSearchGridColumns,
                        widthSizeClass = windowSizeClass.widthSizeClass,
                    )
                }
                val searchGridHorizontalArrangement =
                    Arrangement.spacedBy(searchGridCardLayout.itemSpacingDp.dp)
                val searchCoverRequestSpec = remember(
                    maxWidth, density.density, searchGridCardLayout, searchLayoutPolicy, effectiveSearchGridColumns
                ) {
                    resolveHomeCoverRequestSpec(
                        cardWidthDp = resolveSearchGridCardWidthDp(
                            availableWidthDp = maxWidth.value,
                            minItemWidthDp = searchLayoutPolicy.resultGridMinItemWidthDp.toFloat(),
                            horizontalPaddingDp = searchGridCardLayout.outerPaddingDp.toFloat(),
                            spacingDp = searchGridCardLayout.itemSpacingDp.toFloat(),
                            fixedColumnCount = effectiveSearchGridColumns,
                        ),
                        density = density.density,
                        useLowQualityCover = false,
                    )
                }
                val videoGridModifier = Modifier
                    .then(
                        if (effectiveSearchGridColumns == 1) {
                            Modifier.responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxSize()
                    .homeFeedPinchZoom(
                        enabled = !singleColumn && homeSettings.gridColumnPinchEnabled,
                        currentColumns = effectiveSearchGridColumns,
                        bounds = pinchColumnBounds,
                        onColumnsChange = { newColumns ->
                            gridColumnPreference.onChange(newColumns)
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            isPinchPillVisible = true
                            pinchPillDismissJob?.cancel()
                        },
                        onGestureEnd = { finalColumns ->
                            gridColumnPreference.onGestureEnd(finalColumns)
                            pinchPillDismissJob?.cancel()
                            pinchPillDismissJob = scope.launch {
                                kotlinx.coroutines.delay(1000)
                                isPinchPillVisible = false
                            }
                        }
                    )
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            val showFullCardContent = app.bilipai.core.ui.LocalFullVideoCardContentVisible.current
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(effectiveSearchGridColumns),
                state = activePageGridState,
                contentPadding = PaddingValues(
                    top = resultTopPadding,
                    bottom = resultBottomPadding,
                    start = searchGridCardLayout.outerPaddingDp.dp,
                    end = searchGridCardLayout.outerPaddingDp.dp
                ),
                horizontalArrangement = searchGridHorizontalArrangement,
                verticalItemSpacing = searchGridCardLayout.itemSpacingDp.dp,
                modifier = videoGridModifier
    ) {
            if (targetSearchType == SearchType.ALL) {
                pageResultState.matchResults.firstOrNull()?.let { featuredMatch ->
                    item(key = "all_match_${featuredMatch.id}", span = StaggeredGridItemSpan.FullLine) {
                        MatchSearchResultCard(
                            item = featuredMatch,
                            appearance = genericResultCardAppearance,
                            onClick = {
                                if (featuredMatch.id > 0L) {
                                    onNavigateSearchTarget(BilibiliNavigationTarget.Match(featuredMatch.id))
                                }
                            },
                        )
                    }
                }
                if (pageResultState.upResults.isNotEmpty()) {
                    itemsIndexed(pageResultState.upResults, key = { _, up -> "all_up_${up.mid}" }, span = { _, _ -> StaggeredGridItemSpan.FullLine }) { _, up ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            UpSearchResultCard(
                                upItem = up,
                                appearance = genericResultCardAppearance,
                                onClick = { onUpClick(up.mid) },
                                onLiveClick = { onLiveClick(up.room_id ?: 0L, up.uname, up.uname) },
                                onTransitionClick = onUpResultClick?.let { openResult ->
                                    { key -> openResult(up.mid, key) }
                                },
                            )
                            val relatedVideos = up.res.orEmpty().take(6).map { it.toVideoItem() }
                            if (relatedVideos.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    relatedVideos.forEachIndexed { relatedIndex, video ->
                                        ElegantVideoCard(
                                            video = video,
                                            index = relatedIndex,
                                            modifier = Modifier.width(160.dp),
                                            animationEnabled = false,
                                            motionTier = cardMotionTier,
                                            transitionEnabled = effectiveCardTransitionEnabled,
                                            isReturningFromVideoDetail = isReturningFromVideoDetail,
                                            isQuickReturningFromVideoDetail = isQuickReturningFromVideoDetail,
                                            showPublishTime = true,
                                            glassEnabled = videoCardAppearance.glassEnabled,
                                            blurEnabled = videoCardAppearance.blurEnabled,
                                            showCoverGlassBadges = videoCardAppearance.showCoverGlassBadges,
                                            showInfoGlassBadges = videoCardAppearance.showInfoGlassBadges,
                                            homeDurationStyle = homeDurationStyle,
                                            coverAspectRatio = app.bilipai.core.ui.videoCardCoverAspectRatio(),
                                            highlightedTitle = rememberSearchHighlightedTitle(video),
                                        ) { _, _ ->
                                            when (
                                                val target = resolveVideoSearchNavigationTarget(
                                                    bvid = video.bvid,
                                                    contentType = video.contentType,
                                                    navigationUrl = video.navigationUrl,
                                                    title = video.title,
                                                )
                                            ) {
                                                is SearchResultNavigationTarget.Video -> {
                                                    app.bilipai.core.util.AnalyticsHelper.logSearchResultClick(
                                                        position = relatedIndex,
                                                        resultSection = "creator_related"
                                                    )
                                                    onVideoClick(target.bvid, 0L, video.pic, video.resolveIsVerticalForNavigation())
                                                }
                                                is SearchResultNavigationTarget.Course -> onCheeseClick?.invoke(target.seasonId, target.epId)
                                                    ?: onBangumiClick(target.seasonId)
                                                is SearchResultNavigationTarget.Web -> onWebClick(target.url, target.title)
                                                else -> Unit
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (pageResultState.matchResults.isNotEmpty()) {
                    itemsIndexed(pageResultState.matchResults.drop(1), key = { _, item -> "all_match_${item.id}" }, span = { _, _ -> StaggeredGridItemSpan.FullLine }) { _, item ->
                        MatchSearchResultCard(
                            item = item,
                            appearance = genericResultCardAppearance,
                            onClick = {
                                if (item.id > 0L) {
                                    onNavigateSearchTarget(BilibiliNavigationTarget.Match(item.id))
                                }
                            },
                        )
                    }
                }
                if (pageResultState.bangumiResults.isNotEmpty()) {
                    itemsIndexed(pageResultState.bangumiResults, key = { _, item -> "all_bangumi_${item.seasonId}" }, span = { _, _ -> StaggeredGridItemSpan.FullLine }) { _, item ->
                        BangumiSearchResultCard(
                            item = item,
                            appearance = genericResultCardAppearance,
                            onClick = { onBangumiClick(item.seasonId) },
                            categoryLabel = item.seasonTypeName.ifBlank { "番剧 / 影视" },
                            onTransitionClick = onBangumiResultClick?.let { openResult ->
                                { key ->
                                    if (item.seasonId > 0L) openResult(item.seasonId, key)
                                }
                            },
                        )
                    }
                }
            }
            val searchVideoKeys = pageResultState.searchResults.mapIndexed { index, video ->
                resolveSearchResultLazyItemKey(targetSearchType, index, video.bvid, video.id)
            }
            val renderSearchVideo: @Composable (Int, Modifier) -> Unit = { index, cardModifier ->
                    val video = pageResultState.searchResults[index]
                    // 直播卡片和历史页/直播区走同一套容器转场
                    // （rememberLiveCardContainerTransition），
                    // 之前这里直接 pushNavigation3Key，所以是旧动画。
                    val cardCornerRadius = AppShapes.resolveContainerCornerDp(
                        level = ContainerLevel.Card,
                        uiStyle = LocalAppUiStyle.current,
                    )
                    val liveContainerTransition =
                        if (video.contentType == "live_room") {
                            rememberLiveCardContainerTransition(
                                roomId = video.id,
                                title = video.title,
                                hostName = video.owner.name,
                                color = AppSurfaceTokens.cardContainer(),
                                cornerRadius = cardCornerRadius,
                                onClick = {
                                    onLiveClick(
                                        video.id,
                                        video.title,
                                        video.owner.name
                                    )
                                },
                                enabled = effectiveCardTransitionEnabled,
                            )
                        } else {
                            null
                        }
                    AnimatedVideoListItem(modifier = cardModifier, enabled = cardAnimationEnabled) {
                        val highlightedTitle = rememberSearchHighlightedTitle(video)
                        ElegantVideoCard(
                            video = video,
                            modifier = (if (!showFullCardContent && effectiveSearchGridColumns > 1) Modifier.fillMaxHeight() else Modifier)
                                .then(liveContainerTransition?.modifier ?: Modifier),
                            singleColumn = effectiveSearchGridColumns == 1,
                            index = index,
                            animationEnabled = false, // The stable item wrapper owns column-switch motion.
                            motionTier = cardMotionTier,
                            transitionEnabled = effectiveCardTransitionEnabled,
                            isReturningFromVideoDetail = isReturningFromVideoDetail,
                            isQuickReturningFromVideoDetail = isQuickReturningFromVideoDetail,
                            showPublishTime = true,
                            coverRequestSpec = searchCoverRequestSpec,
                            glassEnabled = videoCardAppearance.glassEnabled,
                            blurEnabled = videoCardAppearance.blurEnabled,
                            showCoverGlassBadges = videoCardAppearance.showCoverGlassBadges,
                            showInfoGlassBadges = videoCardAppearance.showInfoGlassBadges,
                            coverAspectRatio = searchGridCardLayout.coverAspectRatio,
                            compactMetadata = searchGridCardLayout.compactMetadata,
                            compactStatsOnCover = compactVideoStatsOnCover,
                            titleMinLines = 1,
                            homeDurationStyle = homeDurationStyle,
                            highlightedTitle = highlightedTitle,
                            showOnlineCount = showOnlineCount,
                            //  [交互优化] 传递 onWatchLater 用于显示菜单选项
                            onWatchLater = if (video.bvid.isNotBlank()) {
                                { onWatchLater(video.bvid, video.id) }
                            } else {
                                null
                            },
                            onUpClick = onUpClick,
                            onClick = { _, _ ->
                                // 直播项走转场包装的点击（内部会调传入的导航），
                                // 其余沿用原来的目标解析。
                                val liveHandler = liveContainerTransition
                                if (liveHandler != null) {
                                    liveHandler.onClick()
                                } else when (
                                    val target = resolveVideoSearchNavigationTarget(
                                        bvid = video.bvid,
                                        contentType = video.contentType,
                                        navigationUrl = video.navigationUrl,
                                        title = video.title,
                                        videoId = video.id,
                                        ownerName = video.owner.name
                                    )
                                ) {
                                    is SearchResultNavigationTarget.Video -> {
                                        app.bilipai.core.util.AnalyticsHelper.logSearchResultClick(position = index)
                                        onVideoClick(target.bvid, 0, video.pic, video.resolveIsVerticalForNavigation())
                                    }
                                    is SearchResultNavigationTarget.Course -> {
                                        if (onCheeseClick != null) {
                                            onCheeseClick(target.seasonId, target.epId)
                                        } else {
                                            onBangumiClick(target.seasonId)
                                        }
                                    }
                                    is SearchResultNavigationTarget.Web ->
                                        onWebClick(target.url, target.title)
                                    is SearchResultNavigationTarget.LiveRoom ->
                                        onLiveClick(target.roomId, target.title, target.uname)
                                    else -> Unit
                                }
                            }
                        )

                    }
                }
            if (showFullCardContent || effectiveSearchGridColumns == 1) {
                itemsIndexed(
                    pageResultState.searchResults,
                    key = { index, _ -> searchVideoKeys[index] },
                    contentType = { _, video -> "search_video_${video.contentType}" },
                ) { index, _ ->
                    renderSearchVideo(index, videoListItemModifier(enabled = cardAnimationEnabled))
                }
            } else {
                app.bilipai.feature.home.resolveHomeFeedAlignedRows(
                    pageResultState.searchResults.size, effectiveSearchGridColumns,
                ).forEach { row ->
                    item(
                        key = row.joinToString(prefix = "search_video_row_", separator = "_") { searchVideoKeys[it] },
                        contentType = "search_video_row",
                        span = StaggeredGridItemSpan.FullLine,
                    ) {
                        app.bilipai.feature.home.HomeAlignedVideoRow(
                            indices = row,
                            itemKeys = searchVideoKeys,
                            columns = effectiveSearchGridColumns,
                            horizontalArrangement = searchGridHorizontalArrangement,
                            modifier = videoListItemModifier(enabled = cardAnimationEnabled).fillMaxWidth(),
                            content = renderSearchVideo,
                        )
                    }
                }
            }

                // [新增] 空状态提示 (提示可能被屏蔽)
                if (!pageResultState.isSearching && pageResultState.totalCount == 0 && pageResultState.error == null && pageEmptyStateCopy != null) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            app.bilipai.core.ui.EmptyState(
                                message = pageEmptyStateCopy.title,
                                subtitle = pageEmptyStateCopy.subtitle,
                                animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                                enableEasterEgg = false,
                                isVisible = isPageVisible()
                            )
                        }
                    }
                }

                //  [新增] 加载更多指示器
                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pagePresentation.footer == SearchResultFooterMode.ERROR) {
                                SearchLoadMoreIndicator(
                                    error = pageResultState.loadMoreError,
                                    onRetry = onLoadMore
                                )
                            } else {
                                AdaptiveLoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
                            }
                        }
                    }
                }

                //  [新增] 已加载全部提示
                if (pagePresentation.footer == SearchResultFooterMode.END) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        AppText(
                            text = stringResource(R.string.search_all_loaded_count, pageResultState.totalCount),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = AppContentColors.secondaryText(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

                // [新增] 双指缩放切换网格列数 HUD 胶囊 (自适应 MD3 / MIUIX)
                GridPinchColumnHudPill(
                    visible = isPinchPillVisible,
                    columns = effectiveSearchGridColumns,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = resultTopPadding + 16.dp)
                )
            }
        }
        app.bilipai.data.model.response.SearchType.UP -> {
            //  UP主搜索结果
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.upResults,
                    key = { index, upItem ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.UP,
                            index = index,
                            numericKey = upItem.mid
                        )
                    },
                    contentType = { _, _ -> "search_up_card" }
                ) { index, upItem ->
                    UpSearchResultCard(
                        upItem = upItem,
                        appearance = genericResultCardAppearance,
                        onClick = { onUpClick(upItem.mid) },
                        onLiveClick = { onLiveClick(upItem.room_id ?: 0L, upItem.uname, upItem.uname) },
                        onTransitionClick = onUpResultClick?.let { openResult ->
                            { transitionKey: String -> openResult(upItem.mid, transitionKey) }
                        },
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.upResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                 // [新增] 空状态提示
                if (!pageResultState.isSearching && pageResultState.upResults.isEmpty() && pageResultState.error == null && pageEmptyStateCopy != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            app.bilipai.core.ui.EmptyState(
                                message = pageEmptyStateCopy.title,
                                subtitle = pageEmptyStateCopy.subtitle,
                                animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                                enableEasterEgg = false,
                                isVisible = isPageVisible()
                            )
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pagePresentation.footer == SearchResultFooterMode.ERROR) {
                                SearchLoadMoreIndicator(
                                    error = pageResultState.loadMoreError,
                                    onRetry = onLoadMore
                                )
                            } else {
                                AdaptiveLoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.BANGUMI,
        app.bilipai.data.model.response.SearchType.MEDIA_FT -> {
            //  番剧/影视搜索结果
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.bangumiResults,
                    key = { index, bangumiItem ->
                        resolveSearchResultLazyItemKey(
                            searchType = targetSearchType,
                            index = index,
                            numericKey = bangumiItem.seasonId,
                            secondaryNumericKey = bangumiItem.mediaId
                        )
                    },
                    contentType = { _, _ -> "search_bangumi_card" }
                ) { index, bangumiItem ->
                    BangumiSearchResultCard(
                        item = bangumiItem,
                        categoryLabel = targetSearchType.displayName,
                        appearance = genericResultCardAppearance,
                        onClick = {
                            if (bangumiItem.seasonId > 0) {
                                onBangumiClick(bangumiItem.seasonId)
                            }
                        },
                        onTransitionClick = onBangumiResultClick?.let { openResult ->
                            { transitionKey: String ->
                                if (bangumiItem.seasonId > 0L) {
                                    openResult(bangumiItem.seasonId, transitionKey)
                                }
                            }
                        },
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.bangumiResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pagePresentation.footer == SearchResultFooterMode.ERROR) {
                                SearchLoadMoreIndicator(
                                    error = pageResultState.loadMoreError,
                                    onRetry = onLoadMore
                                )
                            } else {
                                AdaptiveLoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
                            }
                        }
                    }
                }

                if (!pageResultState.isSearching && pageResultState.bangumiResults.isEmpty() && pageResultState.error == null && pageEmptyStateCopy != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            app.bilipai.core.ui.EmptyState(
                                message = pageEmptyStateCopy.title,
                                subtitle = pageEmptyStateCopy.subtitle,
                                animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                                enableEasterEgg = false,
                                isVisible = isPageVisible()
                            )
                        }
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.LIVE -> {
            //  直播搜索结果
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.liveResults,
                    key = { index, liveItem ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.LIVE,
                            index = index,
                            numericKey = liveItem.roomid,
                            secondaryNumericKey = liveItem.uid
                        )
                    },
                    contentType = { _, _ -> "search_live_card" }
                ) { index, liveItem ->
                    LiveSearchResultCard(
                        item = liveItem,
                        appearance = genericResultCardAppearance,
                        onClick = { onLiveClick(liveItem.roomid, liveItem.title, liveItem.uname) }
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.liveResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                // [新增] 空状态提示
                if (!pageResultState.isSearching && pageResultState.liveResults.isEmpty() && pageResultState.error == null && pageEmptyStateCopy != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            app.bilipai.core.ui.EmptyState(
                                message = pageEmptyStateCopy.title,
                                subtitle = pageEmptyStateCopy.subtitle,
                                animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                                enableEasterEgg = false,
                                isVisible = isPageVisible()
                            )
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pagePresentation.footer == SearchResultFooterMode.ERROR) {
                                SearchLoadMoreIndicator(
                                    error = pageResultState.loadMoreError,
                                    onRetry = onLoadMore
                                )
                            } else {
                                AdaptiveLoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.LIVE_USER -> {
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.liveUserResults,
                    key = { index, item ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.LIVE_USER,
                            index = index,
                            numericKey = item.uid,
                            secondaryNumericKey = item.roomid
                        )
                    },
                    contentType = { _, _ -> "search_live_user_card" }
                ) { index, item ->
                    LiveUserSearchResultCard(
                        item = item,
                        appearance = genericResultCardAppearance,
                        onClick = {
                            when (val target = resolveLiveUserSearchNavigationTarget(
                                roomId = item.roomid,
                                uid = item.uid,
                                isLive = item.isLive || item.liveStatus == 1,
                                title = item.uname,
                                uname = item.uname
                            )) {
                                is SearchResultNavigationTarget.LiveRoom -> onLiveClick(target.roomId, target.title, target.uname)
                                is SearchResultNavigationTarget.Space -> onUpClick(target.mid)
                                else -> Unit
                            }
                        }
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.liveUserResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        SearchLoadMoreIndicator(
                            error = pageResultState.loadMoreError,
                            onRetry = onLoadMore
                        )
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.ARTICLE -> {
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.articleResults,
                    key = { index, articleItem ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.ARTICLE,
                            index = index,
                            numericKey = articleItem.id
                        )
                    },
                    contentType = { _, _ -> "search_article_card" }
                ) { index, articleItem ->
                    ArticleSearchResultCard(
                        item = articleItem,
                        appearance = genericResultCardAppearance,
                        onClick = { onArticleClick(articleItem.id, articleItem.title) },
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.articleResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                if (!pageResultState.isSearching && pageResultState.articleResults.isEmpty() && pageResultState.error == null && pageEmptyStateCopy != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            app.bilipai.core.ui.EmptyState(
                                message = pageEmptyStateCopy.title,
                                subtitle = pageEmptyStateCopy.subtitle,
                                animation = app.bilipai.core.ui.MaidAnimation.SEARCH_EMPTY,
                                enableEasterEgg = false,
                                isVisible = isPageVisible()
                            )
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (pagePresentation.footer == SearchResultFooterMode.ERROR) {
                                SearchLoadMoreIndicator(
                                    error = pageResultState.loadMoreError,
                                    onRetry = onLoadMore
                                )
                            } else {
                                AdaptiveLoadingIndicator(size = 24.dp, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.TOPIC -> {
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.topicResults,
                    key = { index, item ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.TOPIC,
                            index = index,
                            numericKey = item.topicId
                        )
                    },
                    contentType = { _, _ -> "search_topic_card" }
                ) { index, item ->
                    TopicSearchResultCard(
                        item = item,
                        appearance = genericResultCardAppearance,
                        onClick = {
                            if (item.topicId > 0L) onTopicClick(item.topicId)
                        }
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.topicResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        SearchLoadMoreIndicator(
                            error = pageResultState.loadMoreError,
                            onRetry = onLoadMore
                        )
                    }
                }
            }
        }
        app.bilipai.data.model.response.SearchType.PHOTO -> {
            LazyColumn(
                contentPadding = PaddingValues(top = resultTopPadding, bottom = resultBottomPadding, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = activePageListState,
                modifier = Modifier
                    .responsiveContentWidth(maxWidth = resolveSearchSingleColumnResultMaxWidth())
                    .fillMaxSize()
                    .then(if (searchHazeEnabled) Modifier.hazeSourceCompat(state = hazeState) else Modifier)
            ) {
                itemsIndexed(
                    pageResultState.photoResults,
                    key = { index, item ->
                        resolveSearchResultLazyItemKey(
                            searchType = SearchType.PHOTO,
                            index = index,
                            numericKey = item.id,
                            secondaryNumericKey = item.mid
                        )
                    },
                    contentType = { _, _ -> "search_photo_card" }
                ) { index, item ->
                    PhotoSearchResultCard(
                        item = item,
                        appearance = genericResultCardAppearance
                    )
                    if (targetSearchType == activeSearchType && index == pageResultState.photoResults.size - 3 && pageResultState.hasMoreResults && !pageResultState.isLoadingMore) {
                        LaunchedEffect(pageResultState.currentPage, targetSearchType) {
                            onLoadMore()
                        }
                    }
                }

                if (pagePresentation.footer == SearchResultFooterMode.LOADING || pagePresentation.footer == SearchResultFooterMode.ERROR) {
                    item {
                        SearchLoadMoreIndicator(
                            error = pageResultState.loadMoreError,
                            onRetry = onLoadMore
                        )
                    }
                }
            }
        }
    }
}
}
