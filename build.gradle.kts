// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val objectboxVersion by extra("4.1.0") // For KTS build scripts

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // Android Gradle Plugin 4.1.0 or later supported
        classpath(libs.gradle)
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}