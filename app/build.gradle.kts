plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.sebastienyannis.zarazarao"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sebastienyannis.zarazarao"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        buildFeatures {
            viewBinding = true
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.nanohttpd)
    implementation(libs.nanolets)
    implementation(libs.nanoSockets)
    implementation(libs.jmdns)
    implementation(libs.gson)
    implementation(libs.glide)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}