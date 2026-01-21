plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Only for Kotlin projects
    id("io.objectbox") // Apply last
}

android {
    namespace = "com.example.glimpse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.glimpse"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MAPBOX_PUBLIC_KEY", "\"${project.properties["MAPBOX_PUBLIC_KEY"]}\"")
        buildConfigField("String", "MAPBOX_DOWNLOADS_TOKEN", "\"${project.properties["MAPBOX_DOWNLOADS_TOKEN"]}\"")
        buildConfigField("String", "PICOVOICE_ACCESS_KEY", "\"${project.properties["PICOVOICE_ACCESS_KEY"]}\"")
        buildConfigField("String", "OPENWEATHERMAP_API_KEY", "\"${project.properties["OPENWEATHERMAP_API_KEY"]}\"")
        buildConfigField("String", "GEMINI_KEY", "\"${project.properties["GEMINI_KEY"]}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
        buildConfig = true
    }
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
        )
        )
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.play.services.location)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation(libs.androidx.rules)
    implementation(libs.androidx.espresso.intents)
    testImplementation(libs.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.animation)
    implementation(libs.face.detection)
    implementation(libs.reorderable)

    // tflite
    implementation(libs.litert.gpu)
    implementation(libs.litert.support.api)
    implementation(libs.litert)
    implementation(libs.tasks.vision)
    implementation(libs.coil.compose)

    // Camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.androidx.camera.camera2)

    // mapbox
    implementation(libs.autofill)
    implementation(libs.discover)
    implementation(libs.place.autocomplete)
    implementation(libs.offline)
    implementation(libs.mapbox.search.android)
    implementation(libs.mapbox.search.android.ui)
    implementation(libs.navigationcore)
    implementation(libs.ui.components)

    // picovoice
    implementation(libs.picovoice.android)
    implementation(libs.porcupine.android)

    // gemini
    implementation(libs.generativeai)

    // openweathermap
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(kotlin("test"))
}