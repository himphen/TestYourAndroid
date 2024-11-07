import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.jlleitschuh.gradle.ktlint")
}

tasks.register<Copy>("copyPreCommit") {
    from(file("../.githooks/pre-commit"))
    into(file("../.git/hooks"))
}

gradle.projectsEvaluated {
    tasks.named("preBuild") {
        dependsOn(tasks.named("copyPreCommit"))
    }
}

android {

    compileSdk = 35
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "hibernate.v2.testyourandroid"
        minSdk = 23
        targetSdk = 35
        versionCode = 2311712
        versionName = "Cream Soda with Milk 11.7.1"
        wearAppUnbundled = true
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        val propertiesFile = file("../secrets.properties")
        val properties = Properties()
        properties.load(propertiesFile.inputStream())

        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "ADMOB_BANNER_ID", properties.getProperty("key.admob_banner_id"))
            buildConfigField("String", "ADMOB_HOME_AD_ID", properties.getProperty("key.admob_home_ad_id"))
            buildConfigField("String", "ADMOB_FULL_SCREEN_ID", properties.getProperty("key.admob_full_screen_id"))
            buildConfigField("String", "ADMOB_APP_ID", properties.getProperty("key.admob_app_id"))
            resValue("string", "ADMOB_APP_ID", properties.getProperty("key.admob_app_id"))
            resValue("string", "GOOGLE_API_KEY", properties.getProperty("key.google_api_key"))
            buildConfigField("String", "CONTACT_EMAIL", properties.getProperty("key.contact_email"))
            buildConfigField("String", "CROWDIN_URL", properties.getProperty("key.crowdin_url"))
        }

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "ADMOB_BANNER_ID", properties.getProperty("key.admob_banner_id"))
            buildConfigField("String", "ADMOB_HOME_AD_ID", properties.getProperty("key.admob_home_ad_id"))
            buildConfigField("String", "ADMOB_FULL_SCREEN_ID", properties.getProperty("key.admob_full_screen_id"))
            buildConfigField("String", "ADMOB_APP_ID", properties.getProperty("key.admob_app_id"))
            resValue("string", "ADMOB_APP_ID", properties.getProperty("key.admob_app_id"))
            resValue("string", "GOOGLE_API_KEY", properties.getProperty("key.google_api_key"))
            buildConfigField("String", "CONTACT_EMAIL", properties.getProperty("key.contact_email"))
            buildConfigField("String", "CROWDIN_URL", properties.getProperty("key.crowdin_url"))
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    namespace = "hibernate.v2.testyourandroid"
}

dependencies {
    implementation(project(":draw-library"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Google Android Support
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Koin for Android
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")

    // Google Play Billing Library
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // Google Play Services
    implementation("com.google.android.gms:play-services-ads:23.5.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Coil
    implementation("io.coil-kt:coil:2.4.0")

    // LicensesDialog
    implementation("de.psdev.licensesdialog:licensesdialog:2.2.0")

    // CameraView
    implementation("com.otaliastudios:cameraview:2.7.2")

    // Code Scanner
    implementation("com.github.yuriy-budiyev:code-scanner:2.1.0")

    // Logger
    implementation("com.github.himphen:logger:3.0.1")

    implementation("com.jjoe64:graphview:4.2.2")

    // AndroidVeil
    implementation("com.github.skydoves:androidveil:1.1.3")
}