plugins {
    id("com.android.library")
}

android {
    namespace = "app.bilipai.feature.privacy"
    compileSdk { version = release(37) { minorApiLevel = 0 } }
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin { jvmToolchain(21) }
}

// 纯 Kotlin 策略与数据类：不依赖 :app、导航、设计系统或其它 feature。
