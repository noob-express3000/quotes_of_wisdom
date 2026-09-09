import java.io.File
import java.net.URI
import java.security.MessageDigest

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

val productionRevenueCatApiKey = providers.gradleProperty("REVENUECAT_API_KEY")
    .orNull
    .orEmpty()
val isCiBuild = providers.environmentVariable("CI").orNull.equals("true", ignoreCase = true)
val ciDebugKeystorePath = providers.environmentVariable("CI_DEBUG_KEYSTORE_PATH").orNull

data class BundledQuoteFontSpec(
    val resourceName: String,
    val fontUrl: String,
    val gitBlobSha: String,
    val licenseFileName: String,
    val licenseUrl: String
)

val bundledQuoteFonts = listOf(
    BundledQuoteFontSpec(
        resourceName = "lora_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/lora-bower/master/Lora-Regular.ttf",
        gitBlobSha = "38e77e08885cb1d515d606c29feba0f01031000a",
        licenseFileName = "lora_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/lora-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "cinzel_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/cinzel-bower/master/Cinzel-Regular.ttf",
        gitBlobSha = "c74e54bf29a47d1d58e36f0f2775706e80499e93",
        licenseFileName = "cinzel_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/cinzel-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "cormorant_garamond_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/cormorantgaramond-bower/master/CormorantGaramond-Regular.ttf",
        gitBlobSha = "2909868bd6279028d400a7e4dbb874b563667b0e",
        licenseFileName = "cormorant_garamond_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/cormorantgaramond-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "playfair_display_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/playfairdisplay-bower/master/PlayfairDisplay-Regular.ttf",
        gitBlobSha = "e505263e274d7fe6ace314a0d672a230471d3f64",
        licenseFileName = "playfair_display_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/playfairdisplay-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "libre_baskerville_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/librebaskerville-bower/master/LibreBaskerville-Regular.ttf",
        gitBlobSha = "57e3c23b5250fa5b1e61277e6f47f2d56c3194b8",
        licenseFileName = "libre_baskerville_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/librebaskerville-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "merriweather_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/merriweather-bower/master/Merriweather-Regular.ttf",
        gitBlobSha = "b1747e52aa6dfba8fbb3f3df53d61bf76c5348cd",
        licenseFileName = "merriweather_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/merriweather-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "crimson_text_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/crimsontext-bower/master/CrimsonText-Regular.ttf",
        gitBlobSha = "4dda4ed6617dedffecd1b90c114d100abd617efb",
        licenseFileName = "crimson_text_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/crimsontext-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "alegreya_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/alegreya-bower/master/Alegreya-Regular.ttf",
        gitBlobSha = "8f1ad0a9af7f72352ed919dbbe6cf0e5e2291b74",
        licenseFileName = "alegreya_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/alegreya-bower/master/OFL.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "roboto_slab_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/robotoslab-bower/master/RobotoSlab-Regular.ttf",
        gitBlobSha = "eb52a7907362cc3392eb74892883f5d9e260b638",
        licenseFileName = "roboto_slab_license.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/robotoslab-bower/master/LICENSE.txt"
    ),
    BundledQuoteFontSpec(
        resourceName = "caveat_regular",
        fontUrl = "https://raw.githubusercontent.com/google-fonts-bower/caveat-bower/master/Caveat-Regular.ttf",
        gitBlobSha = "a11f86527e676751a69c54d49d99c24c4f68855d",
        licenseFileName = "caveat_ofl.txt",
        licenseUrl = "https://raw.githubusercontent.com/google-fonts-bower/caveat-bower/master/OFL.txt"
    )
)

val generatedQuoteFontResDir = layout.buildDirectory.dir("generated/quote-fonts/res")
val generatedQuoteFontAssetsDir = layout.buildDirectory.dir("generated/quote-fonts/assets")

val downloadQuoteFonts by tasks.registering {
    group = "build setup"
    description = "Downloads pinned quote fonts and their licenses for offline packaging."
    outputs.dir(generatedQuoteFontResDir)
    outputs.dir(generatedQuoteFontAssetsDir)

    doLast {
        val fontDir = generatedQuoteFontResDir.get().asFile.resolve("font").apply { mkdirs() }
        val licenseDir = generatedQuoteFontAssetsDir.get().asFile.resolve("font_licenses").apply { mkdirs() }

        fun download(url: String, destination: File) {
            destination.parentFile?.mkdirs()
            val connection = URI(url).toURL().openConnection().apply {
                connectTimeout = 20_000
                readTimeout = 45_000
                setRequestProperty("User-Agent", "myquote-build")
            }
            connection.getInputStream().use { input ->
                destination.outputStream().use { output -> input.copyTo(output) }
            }
            check(destination.length() > 0L) { "Downloaded an empty file: $url" }
        }

        fun gitBlobSha(file: File): String {
            val bytes = file.readBytes()
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update("blob ${bytes.size}\u0000".toByteArray(Charsets.UTF_8))
            digest.update(bytes)
            return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
        }

        bundledQuoteFonts.forEach { spec ->
            val fontFile = fontDir.resolve("${spec.resourceName}.ttf")
            download(spec.fontUrl, fontFile)
            check(gitBlobSha(fontFile) == spec.gitBlobSha) {
                "Font integrity check failed for ${spec.resourceName}."
            }

            download(spec.licenseUrl, licenseDir.resolve(spec.licenseFileName))
        }
    }
}

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

    sourceSets.getByName("main") {
        res.srcDir(generatedQuoteFontResDir.get().asFile)
        assets.srcDir(generatedQuoteFontAssetsDir.get().asFile)
    }

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
        versionCode = 3
        versionName = "1.0.2"
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

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(downloadQuoteFonts)
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
