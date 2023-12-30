plugins {
    id("com.android.application")
}

android {
    namespace = "info.staticfree.android.units"
    compileSdk = 17

    defaultConfig {
        applicationId = "info.staticfree.android.units"
        minSdk = 4
        targetSdk = 10
        versionCode = 9
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packaging {
        resources {
            // Not needed as long as we don't use reflection with Kotlin
            excludes.add("**/*.kotlin_builtins")
            excludes.add("**/*.kotlin_module")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(files("libs/android-support-v4.jar"))
    implementation(files("libs/andro-views.jar"))
}