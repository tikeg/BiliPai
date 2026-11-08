// 文件路径: feature/home/HomeComponents.kt
// 此文件包含对话框和错误状态展示
// UserState 定义在 HomeViewModel.kt 中
package app.bilipai.feature.home

import app.bilipai.core.util.LocalWindowSizeClass
import app.bilipai.core.ui.AppContentColors
import app.bilipai.core.ui.resolveFilledButtonContainerColor
import app.bilipai.core.ui.resolveFilledButtonContentColor
import app.bilipai.core.ui.components.AppText

import app.bilipai.core.ui.AppSpacingTokens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.bilipai.core.ui.AppSurfaceTokens
import app.bilipai.core.ui.AppAlertDialog
import app.bilipai.core.ui.components.AppButton
import app.bilipai.core.ui.components.AppSegmentOption
import app.bilipai.core.ui.components.AppThemeAdaptiveTabRow
import app.bilipai.core.ui.components.AppTextButton

// ==========================================
// 对话框组件
// ==========================================

/**
 * 欢迎对话框
 */
@Composable
fun WelcomeDialog(githubUrl: String, onConfirm: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    AppAlertDialog(
        onDismissRequest = { },
        title = { AppText("欢迎") },
        text = {
            Column {
                AppText("本应用仅供学习使用。")
                AppTextButton(
                    onClick = { uriHandler.openUri(githubUrl) },
                    colors = app.bilipai.core.ui.components.AppTextButtonDefaults.primaryColors(),
                ) {
                    AppText("开源地址: $githubUrl", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }
            }
        },
        confirmButton = {
            AppButton(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = resolveFilledButtonContainerColor(MaterialTheme.colorScheme),
contentColor = resolveFilledButtonContentColor(MaterialTheme.colorScheme))) {
                AppText("进入")
            }
        },
        containerColor = AppSurfaceTokens.cardContainer()
    )
}

/**
 * 错误状态展示
 */
@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppText(text = message, color = AppContentColors.error())
            Spacer(modifier = Modifier.height(AppSpacingTokens.Large))
            AppButton(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = resolveFilledButtonContainerColor(MaterialTheme.colorScheme),
contentColor = resolveFilledButtonContentColor(MaterialTheme.colorScheme))
            ) {
                AppText("重试")
            }
        }
    }
}

// ==========================================
// 直播子分类组件
// ==========================================

/**
 *  直播子分类行（关注/热门切换）
 */
@Composable
fun LiveSubCategoryRow(
    selectedSubCategory: LiveSubCategory,
    onSubCategorySelected: (LiveSubCategory) -> Unit
) {
    val options = LiveSubCategory.entries.map { subCategory ->
        AppSegmentOption(
            value = subCategory,
            label = stringResource(resolveLiveSubCategoryLabelRes(subCategory)),
        )
    }
    AppThemeAdaptiveTabRow(
        // Glass indicator fills the measured slot; native geometry keeps its defaults.
        liquidGlassAdaptIndicatorToLabel = !app.bilipai.core.ui.LocalAppThemeConfig.current.liquidGlassEnabled,
        role = app.bilipai.core.ui.components.AppTabRowRole.SECONDARY,
        // Standalone rails center on wide devices in both materials; compact windows keep their anchor.
        centerContent = LocalWindowSizeClass.current.let { it.isTabletDevice && it.isTablet },
        options = options,
        selectedValue = selectedSubCategory,
        onSelectionChange = onSubCategorySelected,
        dragSelectionEnabled = options.size > 1,
        tapPressRefractionEnabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacingTokens.Small),
    )
}
