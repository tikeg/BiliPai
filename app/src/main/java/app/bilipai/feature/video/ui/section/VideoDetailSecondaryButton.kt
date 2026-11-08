package app.bilipai.feature.video.ui.section

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.bilipai.core.theme.AppUiStyle
import app.bilipai.core.theme.LocalAppUiStyle
import app.bilipai.core.ui.components.appElasticPress
import app.bilipai.core.ui.components.AppOutlinedButton
import top.yukonga.miuix.kmp.basic.Button as MiuixButton

/** Secondary actions shared by video notes and AI summary, using each theme's native button. */
@Composable
internal fun VideoDetailSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    when (LocalAppUiStyle.current) {
        AppUiStyle.MATERIAL3 -> AppOutlinedButton(
            onClick = onClick,
            modifier = modifier.appElasticPress(enabled = enabled),
            enabled = enabled,
            content = content
        )
        AppUiStyle.MIUIX -> MiuixButton(
            onClick = onClick,
            modifier = modifier.appElasticPress(enabled = enabled),
            enabled = enabled,
            minHeight = 48.dp,
            content = content
        )
    }
}
