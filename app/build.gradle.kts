plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val productionRevenueCatApiKey = providers.gradleProperty("REVENUECAT_API_KEY")
    .orNull
    .orEmpty()
val isCiBuild = providers.environmentVariable("CI").orNull.equals("true", ignoreCase = true)
val ciDebugKeystorePath = providers.environmentVariable("CI_DEBUG_KEYSTORE_PATH").orNull

val validateProductionConfiguration by tasks.registering {
    group = "verification"
    description = "Rejects release builds without a production RevenueCat Android SDK key."

    doLast {
        check(productionRevenueCatApiKey.startsWith("goog_") &&
            (isCiBuild || !productionRevenueCatApiKey.startsWith("goog_ci_"))) {
            "Release builds require a production RevenueCat Android SDK key " +
                "(-PREVENUECAT_API_KEY=goog_...)."
        }
    }
}

android {
    namespace = "com.shipaton.quotesofwisdom"
    compileSdk = 36

    signingConfigs {
        getByName("debug") {
            ciDebugKeystorePath?.takeIf { it.isNotBlank() }?.let { path ->
                storeFile = file(path)
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    defaultConfig {
        applicationId = "com.shipaton.quotesofwisdom"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "REVENUECAT_API_KEY",
                "\"test_ogkhePOVXjqcVYNHtDMYXrytVkm\""
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "REVENUECAT_API_KEY",
                "\"$productionRevenueCatApiKey\""
            )
        }

        create("qa") {
            initWith(getByName("release"))
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField(
                "String",
                "REVENUECAT_API_KEY",
                "\"test_ogkhePOVXjqcVYNHtDMYXrytVkm\""
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}

tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    dependsOn(validateProductionConfiguration)
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.04.01")

    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("com.revenuecat.purchases:purchases:10.18.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
