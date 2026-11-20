// Configura el módulo Android: Kotlin/Compose, KSP y generación del esquema Room.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// Identidad, SDK, opciones de compilación y características de la app.
android {
    namespace = "com.example.stockscan"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.stockscan"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// Indica dónde Room exporta el esquema de la base de datos.
room {
    schemaDirectory("$projectDir/schemas")
}

// Dependencias de navegación, ciclo de vida, persistencia, cámara y componentes Compose.
dependencies {
    // Navigation Compose conecta las rutas declaradas en Destinos.kt.
    val nav_version = "2.10.2"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")

    // Persistencia local Room; el DAO queda encapsulado dentro de RoomStockScanRepository.
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Splash Screen nativa compatible con Android anteriores a 12.
    implementation(libs.androidx.core.splashscreen)

    // Ubicación y mapa: mismas bibliotecas utilizadas en el ejemplo GeoMapbox del profesor.
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")
    implementation("com.mapbox.maps:android-ndk27:11.16.2")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.16.2")

    // Cámara real requerida por el caso y el ERS: CameraX + lectura ML Kit.
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Componentes visuales y runtime de Jetpack Compose.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
