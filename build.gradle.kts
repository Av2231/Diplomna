buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.google.com/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath ("com.google.gms:google-services:4.3.10")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
