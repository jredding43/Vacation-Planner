plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.jackd424"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.jackd424"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)
    implementation (libs.cardview)

    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.auth)

    implementation (libs.firebase.ui.auth)

    //testing dependencies
    // JUnit for unit tests
    testImplementation (libs.junit)

    // Mockito for mocking
    testImplementation (libs.mockito.core)

    testImplementation (libs.core)
    testImplementation (libs.junit.v113)
}