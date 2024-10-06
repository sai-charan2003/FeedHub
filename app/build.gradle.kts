import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.mikepenz.aboutlibraries.plugin") version "11.2.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"

}



android {
    namespace = "dev.charan.feedhub"
    compileSdk = 34
    val key:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("SUPABASE_ANON_KEY")

    val url:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("SUPABASE_URL")
    val apiKey:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("apiKey")
    val googleId:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty("googleId")



    defaultConfig {

        applicationId = "dev.charan.feedhub"
        minSdk = 26

        targetSdk = 34
        versionCode = 3
        versionName = "3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true

        }



        buildConfigField("String","SUPABASE_ANON_KEY","\"$key\"")
        buildConfigField("String", "SUPABASE_URL", "\"$url\"")
        buildConfigField("String","apiKey","\"$apiKey\"")
        buildConfigField("String","googleId","\"$googleId\"")




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
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.3"
//    }
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
    implementation("androidx.compose.ui:ui-tooling-preview: 1.6.6")
    implementation("androidx.compose.material3:material3-android:1.3.0-alpha05")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-crashlytics:18.6.3")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.prof18.rssparser:rssparser:6.0.6")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.5")
    implementation("io.coil-kt:coil-compose:2.5.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.compose.material:material-icons-extended-android:1.6.5")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")
    implementation ("com.google.dagger:hilt-android:2.48")
    implementation ("me.saket.swipe:swipe:1.3.0")
    kapt ("com.google.dagger:hilt-compiler:2.47")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation ("io.github.jan-tennert.supabase:postgrest-kt:2.2.3")
    implementation ("io.github.jan-tennert.supabase:realtime-kt:2.2.3")
    implementation ("io.github.jan-tennert.supabase:storage-kt:2.2.3")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.2.3")
    implementation("io.github.jan-tennert.supabase:compose-auth:2.2.3")
    implementation ("io.ktor:ktor-client-core:2.3.9")
    implementation ("io.ktor:ktor-utils:2.3.9")
    implementation("io.ktor:ktor-client-cio:2.3.7")
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")
    implementation ("androidx.glance:glance:1.1.0-beta02")
    implementation("androidx.glance:glance-appwidget:1.1.0-beta02")
    implementation("androidx.glance:glance-material3:1.0.0")

    implementation("com.github.mukeshsolanki:MarkdownView-Android:2.0.0")
    implementation("com.meetup:twain:0.2.2")
    implementation ("androidx.work:work-runtime-ktx:2.9.0")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("io.github.kevinnzou:compose-webview-multiplatform:1.8.8")
    implementation ("com.github.ireward:compose-html:1.0.2")

    implementation ("org.jsoup:jsoup:1.13.1")
    implementation ("net.dankito.readability4j:readability4j:1.0.8")
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation( "androidx.credentials:credentials:1.2.2")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation ("com.mikepenz:aboutlibraries-core:11.2.3")
    implementation("com.mikepenz:aboutlibraries-compose-m3:11.2.3")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("com.github.skydoves:balloon-compose:1.6.4")


}