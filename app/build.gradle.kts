plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")

}

android {
    namespace = "com.example.rss_parser"
    compileSdk = 34
    val key:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("SUPABASE_ANON_KEY")

    val url:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("SUPABASE_URL")


    defaultConfig {

        applicationId = "com.example.rss_parser"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true

        }

        buildConfigField("String","SUPABASE_ANON_KEY","\"$key\"")
        buildConfigField("String", "SUPABASE_URL", "\"$url\"")


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
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.2.0-beta01")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.browser:browser:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("com.prof18.rssparser:rssparser:6.0.5")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("io.coil-kt:coil-compose:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.compose.material:material-icons-extended-android:1.5.4")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.27.0")
    implementation ("com.google.dagger:hilt-android:2.44")
    kapt ("com.google.dagger:hilt-compiler:2.44")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation ("io.github.jan-tennert.supabase:postgrest-kt:2.0.2")
    implementation ("io.github.jan-tennert.supabase:realtime-kt:2.0.2")
    implementation ("io.github.jan-tennert.supabase:storage-kt:2.0.2")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.0.2")
    implementation ("io.ktor:ktor-client-core:2.3.7")
    implementation ("io.ktor:ktor-utils:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")
}