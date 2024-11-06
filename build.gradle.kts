// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("kotlin_version", "1.9.21")
        set("koin_version", "3.4.3")
        set("lifecycle_version", "2.8.7")
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}