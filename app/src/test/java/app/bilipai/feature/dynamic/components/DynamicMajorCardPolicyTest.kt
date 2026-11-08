package app.bilipai.feature.dynamic.components

import app.bilipai.data.model.response.CoursesMajor
import app.bilipai.data.model.response.DynamicMajor
import app.bilipai.data.model.response.DynamicMajorBadge
import app.bilipai.data.model.response.DynamicThemeImage
import app.bilipai.data.model.response.MedialistMajor
import app.bilipai.data.model.response.MusicMajor
import app.bilipai.data.model.response.UpowerCommonMajor
import kotlin.test.Test
import kotlin.test.assertEquals

class DynamicMajorCardPolicyTest {

    @Test
    fun musicAndCoursesMapToNativeCardContent() {
        val music = resolveDynamicMajorCard(
            DynamicMajor(
                type = "MAJOR_TYPE_MUSIC",
                music = MusicMajor(title = "单曲", label = "音乐人", jump_url = "music-url"),
            ),
            darkTheme = false,
        )
        val course = resolveDynamicMajorCard(
            DynamicMajor(
                type = "MAJOR_TYPE_COURSES",
                courses = CoursesMajor(title = "Compose 课程", sub_title = "12 节", desc = "已更新"),
            ),
            darkTheme = false,
        )

        assertEquals("音乐", music?.kindLabel)
        assertEquals("music-url", music?.jumpUrl)
        assertEquals("12 节 · 已更新", course?.subtitle)
    }

    @Test
    fun upowerChoosesThemeSpecificAsset() {
        val major = DynamicMajor(
            type = "MAJOR_TYPE_UPOWER_COMMON",
            upower_common = UpowerCommonMajor(
                title = "充电专属内容",
                icon = DynamicThemeImage(dark_src = "dark", light_src = "light"),
            ),
        )

        assertEquals("dark", resolveDynamicMajorCard(major, darkTheme = true)?.cover)
        assertEquals("light", resolveDynamicMajorCard(major, darkTheme = false)?.cover)
    }

    @Test
    fun mediaListUsesServerSubtitleAndBadge() {
        val card = resolveDynamicMajorCard(
            DynamicMajor(
                type = "MAJOR_TYPE_MEDIALIST",
                medialist = MedialistMajor(
                    title = "稍后慢慢看",
                    sub_title = "共 12 个视频",
                    badge = DynamicMajorBadge(text = "收藏夹"),
                ),
            ),
            darkTheme = false,
        )

        assertEquals("共 12 个视频", card?.subtitle)
        assertEquals("收藏夹", card?.kindLabel)
    }
}
