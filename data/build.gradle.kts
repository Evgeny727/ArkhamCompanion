@file:OptIn(ApolloExperimental::class)

import com.apollographql.apollo.annotations.ApolloExperimental

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.arkhamcompanion.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":domain"))

    // Apollo Kotlin
    implementation(libs.apollo.runtime)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.sqlite.bundled)
    ksp(libs.androidx.room.compiler)

    // Paging runtime
    implementation(libs.androidx.paging.runtime.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Serialization for data models
    implementation(libs.kotlinx.serialization.json)

    // Hilt in data layer: runtime and compiler
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

apollo {
    service("service") {
        // The package name for the generated models
        packageName.set("com.arkhamcompanion")
        schemaFiles.from("src/main/graphql/schema.graphqls")
        addTypename.set("always")
        generateApolloEnums.set(true)
        mapScalarToKotlinString("timestamptz")
        mapScalar("jsonb", "kotlinx.serialization.json.JsonElement", "com.arkhamcompanion.data.objects.JsonElementAdapter")
    }
}