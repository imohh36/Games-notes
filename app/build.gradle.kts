import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.net.HttpURLConnection

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.spinel.gamenotes"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  val isPersonal = project.findProperty("isPersonal") == "true"

  defaultConfig {
    applicationId = if (isPersonal) "com.spinel.gamenotes" else "com.spinel.gamesnotes"
    minSdk = 24
    targetSdk = 36
    versionCode = 15
    versionName = "1.4.3"

    manifestPlaceholders["appLabel"] = if (isPersonal) "Game Notes" else "Games notes"
    buildConfigField("boolean", "IS_PERSONAL_FLAVOR", if (isPersonal) "true" else "false")
    buildConfigField("boolean", "ENABLE_ADS", if (isPersonal) "false" else "true")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystoreFile = file("release.keystore").takeIf { it.exists() }
        ?: file("${rootDir}/app/release.keystore").takeIf { it.exists() }
        ?: file("${rootDir}/release.keystore")
      storeFile = keystoreFile
      storePassword = System.getenv("STORE_PASSWORD") ?: "gamenotes123"
      keyAlias = System.getenv("KEY_ALIAS") ?: "release_key"
      keyPassword = System.getenv("KEY_PASSWORD") ?: "gamenotes123"
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  lint {
    abortOnError = false
    checkReleaseBuilds = false
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.process)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  implementation(libs.play.services.ads)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register("copyApkToRoot") {
  doLast {
    val src = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile
    val targetName = project.findProperty("targetApkName") as? String ?: "app-debug.apk"
    val dest = rootProject.layout.projectDirectory.file(targetName).asFile
    if (src.exists()) {
      src.copyTo(dest, overwrite = true)
      println("APK_COPIED_SUCCESS: ${dest.name} -> ${dest.length()} bytes")
    } else {
      println("APK_SRC_NOT_FOUND: ${src.absolutePath}")
    }
  }
}

tasks.register<Zip>("exportProjectZip") {
  archiveFileName.set("GameNotes_v1.4.1_Project.zip")
  destinationDirectory.set(rootProject.layout.projectDirectory.asFile)
  from(rootProject.layout.projectDirectory) {
    exclude("**/build/**")
    exclude(".gradle/**")
    exclude(".git/**")
    exclude("**/*.apk")
    exclude(".build-outputs/**")
  }
}

tasks.register("uploadZipToFileIo") {
  doLast {
    val target = project.findProperty("targetUpload") as? String ?: "GameNotes_v1.4.1_Project.zip"
    val file = rootProject.layout.projectDirectory.file(target).asFile
    println("Uploading ${file.name} (${file.length()} bytes)...")
    try {
      val url = URI("https://tmpfiles.org/api/v1/upload").toURL()
      val boundary = "---Boundary" + System.currentTimeMillis()
      val conn = url.openConnection() as HttpURLConnection
      conn.doOutput = true
      conn.requestMethod = "POST"
      conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
      conn.setConnectTimeout(30000)
      conn.setReadTimeout(180000)

      val outStream = conn.outputStream
      val safeName = file.name.replace(" ", "_")
      val header = "--$boundary\r\nContent-Disposition: form-data; name=\"file\"; filename=\"$safeName\"\r\nContent-Type: application/octet-stream\r\n\r\n"
      outStream.write(header.toByteArray(Charsets.UTF_8))
      file.inputStream().use { input ->
        input.copyTo(outStream)
      }
      val footer = "\r\n--$boundary--\r\n"
      outStream.write(footer.toByteArray(Charsets.UTF_8))
      outStream.flush()
      outStream.close()

      val code = conn.responseCode
      val resp = if (code in 200..299) conn.inputStream.bufferedReader().readText() else conn.errorStream?.bufferedReader()?.readText() ?: "HTTP $code"
      println("TMPFILES_RESULT for ${file.name}: $code -> $resp")
    } catch (e: Exception) {
      println("TMPFILES_ERROR for ${file.name}: ${e.javaClass.simpleName} - ${e.message}")
    }
  }
}


