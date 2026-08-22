plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.gms)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.performance)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arkhamcompanion"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.arkhamcompanion"
        minSdk = 24
        targetSdk = 37
        versionCode = 7
        versionName = "0.1.5-beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        androidResources.localeFilters += setOf("es", "ru", "en", "de", "fr", "pl", "ko", "b+zh+Hant", "b+zh+Hans", "it", "pt", "vi", "uk", "cs",)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":di"))

    //Import In-app updates
    implementation(libs.play.app.update)
    implementation(libs.app.update.ktx)
    implementation(libs.play.services.base)

    //Import splash screen
    implementation(libs.androidx.core.splashscreen)

    //Import Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    //Import Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    //Import charts
    implementation(libs.compose.charts)

    //Import reorderable lists
    implementation(libs.reorderable)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.appcompat.resources)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}