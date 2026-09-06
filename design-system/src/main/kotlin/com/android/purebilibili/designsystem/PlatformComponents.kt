package com.android.purebilibili.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

/**
 * 平台组件适配层（占位实现）
 *
 * 说明：在迁移过程中，先把项目中对 androidx.compose.material3 的直接引用替换为
 * 这些 PlatformX 组件。这样后续只需在这里将实现切换为 MIUIX 的组件即可。
 */

@Composable
fun PlatformButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = Color(0xFF1E88E5), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        Box(modifier = Modifier
            .align(Alignment.Center)
        ) {
            content()
        }
    }
}

@Composable
fun PlatformTopAppBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFFFFFFF))
            .padding(12.dp)
    ) {
        Text(text = title, style = TextStyle(fontSize = 18.sp))
        if (onBack != null) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
fun PlatformTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    // 占位的 TextField 实现：实际迁移时替换为 MIUIX TextField
    if (value.isEmpty()) {
        Text(text = placeholder, style = TextStyle(color = Color.Gray, fontSize = 14.sp))
    } else {
        Text(text = value, style = TextStyle(color = Color.Black, fontSize = 14.sp))
    }
}
