plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {

    compileSdk = 35
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "17"
        // Opt-in option for Koin annotation of KoinComponent.
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    namespace = "hibernate.v2.draw"
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}