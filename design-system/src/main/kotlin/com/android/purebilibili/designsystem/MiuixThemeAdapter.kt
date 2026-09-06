package com.android.purebilibili.designsystem

import androidx.compose.runtime.Composable

/**
 * 占位的 MIUIX 主题适配器。
 *
 * 目的：为全仓迁移建立单一入口。完成迁移时，只需修改此文件以调用 MIUIX 的主题实现，
 * 上层源码无需大规模改动，只需保持对 MiuixTheme 的引用。
 *
 * 当前实现为最小可编译版本（使用 Compose foundation/ui），
 * 并且提供与原 MaterialTheme 相似的简易色彩/排版占位符。
 */
object MiuixTheme {
    @Composable
    fun Theme(content: @Composable () -> Unit) {
        // TODO: 用 MIUIX 官方 Theme 替换此处实现
        // 示例：MiuixTheme(material = ..., content = content)
        content()
    }
}
