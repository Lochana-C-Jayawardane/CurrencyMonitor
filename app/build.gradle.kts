import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
//    alias(libs.plugins.android.application)
}

android {
    namespace = "com.javainstitute.currencymonitor"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.javainstitute.currencymonitor"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val secretPropsFile = rootProject.file("secrets.properties")
        val secretProps = Properties() // Now this reference will be resolved

        if (secretPropsFile.exists()) {
            secretPropsFile.inputStream().use { stream ->
                secretProps.load(stream) // Securely load the file
            }
        }

        // 2. Create the field for Java to use
        // We get the value from the key you wrote: CURRENCY_API_KEY
        val apiKey = secretProps.getProperty("CURRENCY_API_KEY") ?: ""
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
//    implementation(libs.wearable)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ... other dependencies ...
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// Material Design 3
    implementation("com.google.android.material:material:1.12.0")
}