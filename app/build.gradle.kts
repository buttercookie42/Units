import com.android.build.api.dsl.ApkSigningConfig

plugins {
    id("com.android.application")
    id("com.sidneysimmons.gradle-plugin-external-properties")
}

externalProperties {
    propertiesFileResolver(file("signing.properties"))
}

android {
    namespace = "de.buttercookie.units"
    compileSdk = 17

    defaultConfig {
        applicationId = "de.buttercookie.units"
        minSdk = 4
        targetSdk = 10
        versionCode = 10
        versionName = "1.1a1"
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

    signingConfigs {
        named("debug") {
            if (checkExternalSigningConfig()) {
                applyExternalSigningConfig()
            } else {
                defaultConfig.signingConfig
            }
        }
        create("release") {
            if (checkExternalSigningConfig()) {
                applyExternalSigningConfig()
                android.buildTypes.getByName("release").signingConfig = this
            }
        }
    }
}

dependencies {
    implementation(files("libs/android-support-v4.jar"))
    implementation(files("libs/andro-views.jar"))
}

fun ApkSigningConfig.checkExternalSigningConfig(): Boolean {
    return props.exists(name + ".keyStore") &&
            file(props.get(name + ".keyStore")).exists() &&
            props.exists(name + ".storePassword") &&
            props.exists(name + ".keyAlias") &&
            props.exists(name + ".keyPassword")
}

fun ApkSigningConfig.applyExternalSigningConfig() {
    storeFile = file(props.get(name + ".keyStore"))
    storePassword = props.get(name + ".storePassword")
    keyAlias = props.get(name + ".keyAlias")
    keyPassword = props.get(name + ".keyPassword")
}
