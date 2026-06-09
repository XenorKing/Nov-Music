plugins {
      alias(libs.plugins.android.application)
      alias(libs.plugins.kotlin.android)
      alias(libs.plugins.kotlin.compose)
      alias(libs.plugins.google.services)
      alias(libs.plugins.hilt)
      alias(libs.plugins.kotlin.serialization)
      kotlin("kapt")
  }

  android {
      namespace = "com.novmusic"
      compileSdk = 35

      defaultConfig {
          applicationId = "com.novmusic"
          minSdk = 26
          targetSdk = 35
          versionCode = 1
          versionName = "1.0.0"
          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      }

      signingConfigs {
          create("release") {
              storeFile = file(project.findProperty("KEYSTORE_PATH") ?: "keystore.jks")
              storePassword = project.findProperty("KEYSTORE_PASS") as String? ?: ""
              keyAlias = project.findProperty("KEY_ALIAS") as String? ?: ""
              keyPassword = project.findProperty("KEY_PASS") as String? ?: ""
          }
      }

      buildTypes {
          release {
              isMinifyEnabled = true
              isShrinkResources = true
              proguardFiles(
                  getDefaultProguardFile("proguard-android-optimize.txt"),
                  "proguard-rules.pro"
              )
              signingConfig = signingConfigs.getByName("release")
          }
          debug {
              isDebuggable = true
          }
      }

      compileOptions {
          sourceCompatibility = JavaVersion.VERSION_17
          targetCompatibility = JavaVersion.VERSION_17
      }
      kotlinOptions { jvmTarget = "17" }
      buildFeatures { compose = true }
  }

  dependencies {
      implementation(libs.androidx.core.ktx)
      implementation(libs.androidx.lifecycle.runtime.ktx)
      implementation(libs.androidx.lifecycle.viewmodel.compose)
      implementation(libs.androidx.activity.compose)
      implementation(platform(libs.androidx.compose.bom))
      implementation(libs.androidx.ui)
      implementation(libs.androidx.ui.graphics)
      implementation(libs.androidx.ui.tooling.preview)
      implementation(libs.androidx.material3)
      implementation(libs.androidx.material.icons.extended)
      implementation(libs.androidx.navigation.compose)

      implementation(libs.hilt.android)
      kapt(libs.hilt.compiler)
      implementation(libs.hilt.navigation.compose)

      implementation(platform(libs.firebase.bom))
      implementation(libs.firebase.auth)
      implementation(libs.firebase.firestore)

      implementation(libs.retrofit)
      implementation(libs.retrofit.serialization)
      implementation(libs.okhttp)
      implementation(libs.okhttp.logging)

      implementation(libs.coil.compose)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.coroutines.android)
      implementation(libs.kotlinx.coroutines.play.services)
      implementation(libs.androidx.datastore.preferences)

      implementation(libs.media3.exoplayer)
      implementation(libs.media3.ui)
      implementation(libs.media3.session)

      debugImplementation(libs.androidx.ui.tooling)
  }

  kapt { correctErrorTypes = true }
  