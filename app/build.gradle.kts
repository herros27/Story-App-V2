plugins {
    id("com.android.application")
    id("kotlin-android")
    id ("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.storyapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 2
        versionName = "2.0"
        buildConfigField("String","BASE_URL","\"https://story-api.dicoding.dev/v1/\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.test.ext:junit-ktx:1.2.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-basement:18.4.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.compose.ui:ui-graphics-android:1.7.5")

//    ui test
    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    androidTestImplementation("com.squareup.okhttp3:okhttp-tls:4.9.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")
    implementation ("androidx.exifinterface:exifinterface:1.3.6")
    implementation ("com.google.android.gms:play-services-location:18.0.0")

//    test
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")

    //room
    testImplementation("androidx.room:room-testing:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1") // Wajib untuk runtime
    ksp("androidx.room:room-compiler:2.6.1")


    //paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.4")
    implementation( "androidx.room:room-paging:2.6.1")
}