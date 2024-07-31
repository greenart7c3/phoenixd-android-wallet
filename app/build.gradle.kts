plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ktlint)
    kotlin("kapt") version "2.0.0"
}

android {
    namespace = "com.greenart7c3.phoenixd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.greenart7c3.phoenixd"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "0.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            debug {
                applicationIdSuffix = ".debug"
                versionNameSuffix = "-DEBUG"
                resValue("string", "app_name", "@string/app_name_debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.viewbinding)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.extended.icons)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.security.crypto.ktx)
    implementation(libs.ktor.client)
    implementation(libs.ktor.cio)
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.jna) {
        artifact { type = "aar" }
    }
    implementation(libs.acinq.lightning.kmp)
    implementation(libs.secp256k1.kmp)
}
