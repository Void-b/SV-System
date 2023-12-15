import java.util.regex.Pattern.compile

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sv_system"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.sv_system"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-storage:20.3.0")
//    implementation("com.google.android.material:material-icons:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.androidmads:QRGenerator:1.0.1")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation ("com.google.zxing:core:3.4.1") // Or the latest version
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
}