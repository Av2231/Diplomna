plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.pmu"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pmu"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    annotationProcessor("org.androidannotations:androidannotations:4.7.0")
    implementation("org.androidannotations:androidannotations-api:4.7.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.loopj.android:android-async-http:1.4.10")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("com.appolica:interactive-info-window-android:1.1.0")

}