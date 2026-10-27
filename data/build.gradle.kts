@file:OptIn(ApolloExperimental::class)

import com.apollographql.apollo.annotations.ApolloExperimental
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
    alias(libs.plugins.kotlin.serialization)
}

val secretsFile = rootProject.file("secrets.properties")

val secrets = Properties().apply {
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { load(it) }
    }
}

fun secret(name: String): String =
    secrets.getProperty(name).orEmpty()

fun buildConfigString(value: String): String =
    "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "")
        .replace("\r", "") + "\""

android {
    namespace = "com.arkhamcompanion.data"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "ARKHAM_CLIENT_ID",
            buildConfigString(secret("ARKHAM_CLIENT_ID")),
        )

        buildConfigField(
            "String",
            "ARKHAM_CLIENT_SECRET",
            buildConfigString(secret("ARKHAM_CLIENT_SECRET")),
        )

        buildConfigField(
            "String",
            "ARKHAM_REDIRECT_URI",
            buildConfigString(
                secrets.getProperty(
                    "ARKHAM_REDIRECT_URI",
                    "arkhamcompanion://arkhambuild/redirect",
                )
            ),
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(project(":domain"))

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //OAuth
    implementation(libs.oauth)

    // Apollo Kotlin
    implementation(libs.apollo.runtime)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)

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