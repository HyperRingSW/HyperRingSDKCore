// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
//    `maven-publish`
}

buildscript {
    dependencies {
        classpath (libs.android.maven.gradle.plugin)

    }
}

//url = "https://github.com/HyperRingSW/HyperRingSDKCore"