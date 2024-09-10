plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.jyleon.scrolltext"
    compileSdk = 34

    defaultConfig {
        minSdk = 22

        aarMetadata {
            minCompileSdk = 22
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.jyleon.scrolltext"
            artifactId = "scrolltext"
            version = "1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

