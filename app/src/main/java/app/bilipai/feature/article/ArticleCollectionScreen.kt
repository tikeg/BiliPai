@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package app.bilipai.feature.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import app.bilipai.core.ui.AppTopBar
import app.bilipai.core.ui.ImmersiveAppScaffold
import app.bilipai.core.ui.rememberAppBackIcon
import app.bilipai.core.ui.rememberAppMoreIcon
import app.bilipai.core.ui.components.*
import app.bilipai.core.util.responsiveContentWidth
import app.bilipai.data.model.response.ArticleCollectionInfo

@Composable
internal fun ArticleCollectionScreen(
    collectionId: Long,
    initialTitle: String,
    onBack: () -> Unit,
    onUserClick: (Long) -> Unit,
    onArticleClick: (Long, String) -> Unit,
    onOpenWeb: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentArticleId: Long = 0L,
) {
    val context = LocalContext.current
    var showMenu by remember(collectionId) { mutableStateOf(false) }
    var resolvedTitle by remember(collectionId, initialTitle) { mutableStateOf(initialTitle) }
    val url = "https://www.bilibili.com/read/mobile-readlist/rl$collectionId"
    val seed = remember(collectionId, initialTitle) { ArticleCollectionInfo(id = collectionId, name = initialTitle) }
    ImmersiveAppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = resolvedTitle.ifBlank { "专栏文集" },
                navigationIcon = { AppIconButton(onClick = onBack) { AppIcon(rememberAppBackIcon(), contentDescription = "返回") } },
                actions = {
                    AppGlassMenuAnchor(onOpen = { showMenu = true }) {
                        AppIconButton(onClick = { showMenu = true }) { AppIcon(rememberAppMoreIcon(), contentDescription = "更多文集操作") }
                        AppDropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            AppDropdownMenuItem(text = { AppText("分享文集") }, onClick = {
                                showMenu = false
                                val share = Intent(Intent.ACTION_SEND).setType("text/plain")
                                    .putExtra(Intent.EXTRA_TEXT, "${resolvedTitle.ifBlank { "专栏文集" }}\n$url")
                                context.startActivity(Intent.createChooser(share, "分享文集"))
                            })
                            AppDropdownMenuItem(text = { AppText("复制链接") }, onClick = {
                                showMenu = false
                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                    .setPrimaryClip(ClipData.newPlainText("文集链接", url))
                                Toast.makeText(context, "已复制链接", Toast.LENGTH_SHORT).show()
                            })
                            AppDropdownMenuItem(text = { AppText("浏览器打开") }, onClick = {
                                showMenu = false
                                onOpenWeb(url)
                            })
                        }
                    }
                },
            )
        },
    ) { padding ->
        app.bilipai.core.ui.adaptive.AppHingeSafeContent(Modifier.fillMaxSize()) {
            ArticleCollectionContent(
                seed = seed,
                currentArticleId = currentArticleId,
                modifier = Modifier.fillMaxSize().padding(padding).responsiveContentWidth(),
                onAuthorClick = onUserClick,
                onTitleResolved = { resolvedTitle = it },
                onArticleClick = onArticleClick,
            )
        }
    }
}
