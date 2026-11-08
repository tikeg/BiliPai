plugins {
    `java-gradle-plugin`
}

// compileOnly 按官方 gradle-recipes 模式：本组合构建的插件类在应用时与 AGP 同一插件类加载器，
// 接口运行期可见。不要改回 buildSrc —— buildSrc 的 compileOnly 会在 daemon 中报
// Unable to load class，implementation 又会把 gradle-api 的 AppPlugin 等类泄漏进主构建
// classpath，触发 "plugin is already on the classpath with an unknown version"。
dependencies {
    compileOnly("com.android.tools.build:gradle-api:${libs.versions.agp.get()}")
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-commons:9.8")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.android.tools.build:gradle-api:${libs.versions.agp.get()}")
}

gradlePlugin {
    plugins {
        create("composeClipboardReadGuard") {
            id = "app.bilipai.compose-clipboard-read-guard"
            implementationClass = "app.bilipai.build.ComposeClipboardReadGuardPlugin"
        }
        create("composePopupWindowGuard") {
            id = "app.bilipai.compose-popup-window-guard"
            implementationClass = "app.bilipai.build.ComposePopupWindowGuardPlugin"
        }
        create("navigationEventDisposalGuard") {
            id = "app.bilipai.navigationevent-disposal-guard"
            implementationClass = "app.bilipai.build.NavigationEventDisposalGuardPlugin"
        }
        create("composeDragStartGuard") {
            id = "app.bilipai.compose-drag-start-guard"
            implementationClass = "app.bilipai.build.ComposeDragStartGuardPlugin"
        }
        create("composeDetachedOwnerGuard") {
            id = "app.bilipai.compose-detached-owner-guard"
            implementationClass = "app.bilipai.build.ComposeDetachedOwnerGuardPlugin"
        }
        create("workManagerNamespaceGuard") {
            id = "app.bilipai.workmanager-namespace-guard"
            implementationClass = "app.bilipai.build.WorkManagerNamespaceGuardPlugin"
        }
    }
}
