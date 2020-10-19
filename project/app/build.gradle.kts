plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "3.0.0"
    id("io.objectbox")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.hiya.jacoco-android")
}

val versionMajor = 2
val versionMinor = 2
val versionPatch = 1
val versionBuild = 0 // This value is managed by the gradle publisher plugin. Build numbers get incremented on publish
val googleMapsAPIKey = extra.get("google_maps_api_key")?.toString() ?: "PLACEHOLDER_API_KEY"

android {
    compileSdkVersion(30)
    //TODO wait for google to fix bug in AGP 4.0.0: https://issuetracker.google.com/issues/144111441
    ndkVersion = "21.3.6528147"

    defaultConfig {
        applicationId = "org.owntracks.android"
        minSdkVersion(21)
        targetSdkVersion(30)

        versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("eventBusIndex" to "org.owntracks.android.EventBusIndex"))
            }
        }
        resConfigs("en", "de", "fr", "es", "ru")
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
/* TODO Get this lot sorted when the orchestrator / coverage / clearPackageData bug gets fixed */
//        testInstrumentationRunnerArguments clearPackageData: "true"
//        testInstrumentationRunnerArguments coverageFilePath: "/sdcard/"
//        testInstrumentationRunnerArguments coverage: "true"
    }

    signingConfigs {
        register("release") {
            keyAlias = "upload"
            keyPassword = System.getenv("KEYSTORE_PASSPHRASE")
            storeFile = file("../owntracks.release.keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSPHRASE")
        }
    }

    buildTypes {

        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isZipAlignEnabled = true
            proguardFiles = mutableListOf(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
            resValue("string", "GOOGLE_MAPS_API_KEY", googleMapsAPIKey)
            signingConfig = signingConfigs.findByName("release")
        }

        named("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isZipAlignEnabled = true
            proguardFiles = mutableListOf(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
            resValue("string", "GOOGLE_MAPS_API_KEY", googleMapsAPIKey)
            applicationIdSuffix = ".debug"
            isTestCoverageEnabled = true
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/notice.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/dependencies.txt")
        exclude("META-INF/LGPL2.1")
        exclude("META-INF/proguard/androidx-annotations.pro")
    }

    lintOptions {
        baselineFile = file("../../lint/lint-baseline.xml")
        isCheckAllWarnings = true
        isWarningsAsErrors = false
        isAbortOnError = false
        disable("TypographyFractions", "TypographyQuotes", "Typos")
    }
    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isIncludeAndroidResources = true
        }
//        execution "ANDROIDX_TEST_ORCHESTRATOR"
    }

    tasks.withType<Test> {
        testLogging {
            events("passed", "skipped", "failed")
            setExceptionFormat("full")
        }
        reports.junitXml.isEnabled = true
        reports.html.isEnabled = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildToolsVersion = "29.0.3"
}

kapt {
    correctErrorTypes = true
}

val daggerVersion = "2.29.1"
val okHttpVersion = "4.9.0"
val jacksonVersion = "2.11.3"
val materialDialogsVersion = "0.9.6.0"
val objectboxVersion = "2.7.1"
val espressoVersion = "3.3.0"
val androidxTestVersion = "1.3.0"
val kotlinCoroutinesVersion = "1.3.9"

dependencies {
    implementation("androidx.preference:preference:1.1.1")
    implementation("com.takisoft.preferencex:preferencex:1.1.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.work:work-runtime:2.4.0")

    // Play Services libraries
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("com.google.android.gms:play-services-location:17.1.0")

    // Utility libraries
    implementation("com.google.dagger:dagger:${daggerVersion}")
    implementation("com.google.dagger:dagger-android-support:${daggerVersion}")
    implementation("com.google.dagger:dagger-android:${daggerVersion}")

    implementation("org.greenrobot:eventbus:3.2.0")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okHttpVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.github.joshjdevl.libsodiumjni:libsodium-jni-aar:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${kotlinCoroutinesVersion}")

    // Widget libraries
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("com.mikepenz:materialdrawer:6.1.2@aar") { isTransitive = true }
    implementation("com.mikepenz:materialize:1.2.1@aar")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${KotlinVersion.CURRENT}")

    implementation("io.objectbox:objectbox-android:$objectboxVersion")
    kapt("io.objectbox:objectbox-processor:$objectboxVersion")
    // some useful Kotlin extension functions
    implementation("io.objectbox:objectbox-kotlin:$objectboxVersion")

    // These Java EE libs are no longer included in JDKs, so we include explicitly
    kapt("javax.xml.bind:jaxb-api:2.3.1")
    kapt("com.sun.xml.bind:jaxb-core:2.3.0.1")
    kapt("com.sun.xml.bind:jaxb-impl:2.3.2")

    // Preprocessors
    kapt("org.greenrobot:eventbus-annotation-processor:3.2.0")
    kapt("com.google.dagger:dagger-compiler:${daggerVersion}")
    kapt("com.google.dagger:dagger-android-processor:${daggerVersion}")

    kaptTest("com.google.dagger:dagger-compiler:${daggerVersion}")
    kaptTest("com.google.dagger:dagger-android-processor:${daggerVersion}")

    testImplementation("org.powermock:powermock-module-junit4:2.0.7")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.7")
    testImplementation("androidx.test:core:${androidxTestVersion}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    androidTestImplementation("androidx.test.espresso:espresso-core:${espressoVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:${espressoVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-intents:${espressoVersion}")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test:core-ktx:${androidxTestVersion}")
    androidTestImplementation("com.schibsted.spain:barista:3.7.0") {
        exclude("org.jetbrains.kotlin")
    }

    androidTestImplementation("androidx.test:rules:${androidxTestVersion}")
    androidTestImplementation("androidx.test:runner:${androidxTestVersion}")
    androidTestUtil("androidx.test:orchestrator:${androidxTestVersion}")
}


// Publishing
val serviceAccountCreds = file("owntracks-android-7a8e1517bde3.json")

play {
    if (serviceAccountCreds.exists()) {
        enabled.set(true)
        serviceAccountCredentials.set(serviceAccountCreds)
    } else {
        enabled.set(false)
    }
    track.set("internal")

    resolutionStrategy.set(com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO)
}