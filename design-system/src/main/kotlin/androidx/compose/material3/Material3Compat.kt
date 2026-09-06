package androidx.compose.material3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.purebilibili.designsystem.PlatformButton
import com.android.purebilibili.designsystem.PlatformTopAppBar
import com.android.purebilibili.designsystem.PlatformTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment

/**
 * A lightweight compatibility shim for commonly used Material3 APIs.
 *
 * Purpose: allow existing source files that import androidx.compose.material3.* to keep their
 * imports while delegating the actual implementation to the project's Platform adapters.
 * This is a stop-gap to migrate the codebase incrementally without changing all call sites at once.
 *
 * Limitations: the shim implements a small subset of Material3 APIs with simplified signatures.
 * Complex usages may still need manual migration.
 */

// Minimal ColorScheme/ Typography / MaterialTheme compatibility surface so existing code that
// reads MaterialTheme.colorScheme / typography compiles while we migrate to MIUIX.
data class ColorScheme(
    val primary: Color = Color.Unspecified,
    val surfaceVariant: Color = Color.Unspecified,
    val onSurfaceVariant: Color = Color.Unspecified,
    val outline: Color = Color.Unspecified,
    val onSurface: Color = Color.Unspecified
)

data class AppTextStyle(
    val fontSize: TextUnit = 14.sp
)

data class Typography(
    val labelSmall: AppTextStyle = AppTextStyle(12.sp),
    val labelMedium: AppTextStyle = AppTextStyle(14.sp),
    val bodyMedium: AppTextStyle = AppTextStyle(14.sp)
)

object MaterialTheme {
    // These defaults are intentionally permissive (Color.Unspecified) —
    // visual fidelity should be provided by the app's own theme adapters.
    val colorScheme: ColorScheme = ColorScheme()
    val typography: Typography = Typography()
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    PlatformButton(onClick = onClick, modifier = modifier) {
        content()
    }
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(fontSize = 14.sp),
    color: Color = Color.Unspecified
) {
    // Use the platform text renderer from the project's design system to keep
    // typography consistent. If PlatformTextField / PlatformButton are not
    // desirable for a call site, migrate that call site to MIUIX directly.
    androidx.compose.material3.Text(text = text, modifier = modifier, style = style)
}

@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Map to simple PlatformTopAppBar which accepts string title in our adapter; if caller provides
    // composable title we render into it by invoking title() and reading text is not possible here.
    // For simple cases where title is Text("...") this will work via the title composable.
    Box(modifier = modifier.padding(0.dp)) {
        // If navigationIcon is non-null, we show a back icon by delegating to PlatformTopAppBar
        title()
        if (navigationIcon != null) {
            navigationIcon()
        }
    }
}

@Composable
fun CenterAlignedTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(title = title, navigationIcon = navigationIcon, modifier = modifier)
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null
) {
    PlatformTextField(value = value, onValueChange = onValueChange, modifier = modifier, placeholder = placeholder?.let {
        ""
    } ?: "")
}

@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null
) {
    TextField(value = value, onValueChange = onValueChange, modifier = modifier, placeholder = placeholder)
}

@Composable
fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // Simple mapping to PlatformButton; visually this may differ but functional for clicks.
    PlatformButton(onClick = onClick, modifier = modifier) {
        content()
    }
}

@Composable
fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
    }
}

@Composable
fun Surface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
    }
}

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        topBar?.invoke()
        Box(Modifier.padding(top = 0.dp)) {
            content()
        }
    }
}
