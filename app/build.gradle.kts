plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sbtv"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.sbtv"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)


    //Networking - OkHTTP
    implementation(libs.okhttp)
    implementation(libs.androidx.compose.remote.creation.core)

    //Extra Icons
    implementation(libs.androidx.compose.material.icons.extended)

    //Navigation
    implementation(libs.navigation.compose)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Gson - JSON
    implementation(libs.gson)
    
    //Coil - Image Loading
    implementation(libs.coil.compose)

    //Coil - Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // VLC Player
    implementation("org.videolan.android:libvlc-all:3.6.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}