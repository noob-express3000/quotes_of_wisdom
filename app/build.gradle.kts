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

fun googleFontsBowerFont(
    resourceName: String,
    repository: String,
    fileName: String,
    gitBlobSha: String,
    license: String = "OFL"
): BundledQuoteFontSpec {
    val licensePath = if (license == "APACHE2") "LICENSE.txt" else "OFL.txt"
    val licenseSuffix = if (license == "APACHE2") "license" else "ofl"
    val base = "https://raw.githubusercontent.com/google-fonts-bower/$repository/master"
    return BundledQuoteFontSpec(
        resourceName = resourceName,
        fontUrl = "$base/$fileName",
        gitBlobSha = gitBlobSha,
        licenseFileName = "${resourceName}_$licenseSuffix.txt",
        licenseUrl = "$base/$licensePath"
    )
}

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
    ),
    googleFontsBowerFont("noto_serif_regular", "notoserif-bower", "NotoSerif-Regular.ttf", "96c7d74ab69adb5a8d61d2449cb3c9c6cf63bfb8"),
    googleFontsBowerFont("source_serif_pro_regular", "sourceserifpro-bower", "SourceSerifPro-Regular.ttf", "a9b478f4e83cb38a8a42103c7763e1adedc54804"),
    googleFontsBowerFont("libre_caslon_text_regular", "librecaslontext-bower", "LibreCaslonText-Regular.ttf", "5f53a47d599cbd223f8db21e2aafe9cf2ee562c9"),
    googleFontsBowerFont("uncial_antiqua_regular", "uncialantiqua-bower", "UncialAntiqua-Regular.ttf", "4e801dc5faa225e11cab23ebe99c7daa47e0a76d"),
    googleFontsBowerFont("neuton_regular", "neuton-bower", "Neuton-Regular.ttf", "3abe95222fdb15483fb49bb97f3291dc1ee492a6"),
    googleFontsBowerFont("enriqueta_regular", "enriqueta-bower", "Enriqueta-Regular.ttf", "466db7e858344ce52b9abbda0712a91062a74271"),
    googleFontsBowerFont("coustard_regular", "coustard-bower", "Coustard-Regular.ttf", "3ce93facef721760e5a35abd306f21803984b13a"),
    googleFontsBowerFont("gilda_display_regular", "gildadisplay-bower", "GildaDisplay-Regular.ttf", "ee5a40cdfb1fb74a469f0a9dac346a72b7c682fc"),
    googleFontsBowerFont("bree_serif_regular", "breeserif-bower", "BreeSerif-Regular.ttf", "bb069cfcfe5c716faadfdf1fb9e733e251249c1d"),
    googleFontsBowerFont("kurale_regular", "kurale-bower", "Kurale-Regular.ttf", "66424111757247a7ade759285a2e70d1906ee5cb"),
    googleFontsBowerFont("philosopher_regular", "philosopher-bower", "Philosopher-Regular.ttf", "de5d78b2bafe2379ef7112c8615a6b72d0172cf0"),
    googleFontsBowerFont("andada_regular", "andada-bower", "Andada-Regular.ttf", "99c35604b7cfa9703077b37d8826ebbb8f82f94c"),
    googleFontsBowerFont("averia_serif_libre_regular", "averiaseriflibre-bower", "AveriaSerifLibre-Regular.ttf", "fbc33d56962cf2cfc8705a6b333381d8699e1dcc"),
    googleFontsBowerFont("lusitana_regular", "lusitana-bower", "Lusitana-Regular.ttf", "5a8eccae8ab6bfe0ba3f7fcc0ca17829c649dd8e"),
    googleFontsBowerFont("kotta_one_regular", "kottaone-bower", "KottaOne-Regular.ttf", "79001e263a6a2e121cd69a80eaf37d393a8bceba"),
    googleFontsBowerFont("caudex_regular", "caudex-bower", "Caudex-Regular.ttf", "669f9f8c9d375158b9b2a9ff29de0070017e87cd"),
    googleFontsBowerFont("cantata_one_regular", "cantataone-bower", "CantataOne-Regular.ttf", "b31d2a704ef0d72de1e7a747208be8812f07e24b"),
    googleFontsBowerFont("abril_fatface_regular", "abrilfatface-bower", "AbrilFatface-Regular.ttf", "e761f7b9c3c77290511fd87c296c8a3077d5db44"),
    googleFontsBowerFont("josefin_slab_regular", "josefinslab-bower", "JosefinSlab-Regular.ttf", "6cb8832fa6a4df47c6dd5ffb1723360678ba4ac7"),
    googleFontsBowerFont("roboto_mono_regular", "robotomono-bower", "RobotoMono-Regular.ttf", "495a82ce92ede816ffde602ade57dc02dc7b6314", "APACHE2"),
    googleFontsBowerFont("cutive_mono_regular", "cutivemono-bower", "CutiveMono-Regular.ttf", "980fab95b773bbbe5fa0237abb5464d93edbc337"),
    googleFontsBowerFont("special_elite_regular", "specialelite-bower", "SpecialElite.ttf", "b62fddb4baf5b67db8996114315dc18cfbf594cc", "APACHE2"),
    googleFontsBowerFont("architects_daughter_regular", "architectsdaughter-bower", "ArchitectsDaughter.ttf", "a2d4f0c46d716ad5db0516116aa46a7d9744679c"),
    googleFontsBowerFont("permanent_marker_regular", "permanentmarker-bower", "PermanentMarker.ttf", "8052508cfd6ac4617327bcdf7726aafda67a4bab", "APACHE2"),
    googleFontsBowerFont("shadows_into_light_regular", "shadowsintolight-bower", "ShadowsIntoLight.ttf", "903e32be5e3dbc418dea268b28bc3432bde14a10"),
    googleFontsBowerFont("dancing_script_regular", "dancingscript-bower", "DancingScript-Regular.ttf", "72db83506c68815348b1588f353a8081b08df412"),
    googleFontsBowerFont("great_vibes_regular", "greatvibes-bower", "GreatVibes-Regular.ttf", "9dbbf4df5ee7411ec19752c90cde2fa7e991fd9c"),
    googleFontsBowerFont("kaushan_script_regular", "kaushanscript-bower", "KaushanScript-Regular.ttf", "e69e4ac0192b8890c1d3a3aeb1df731df9de6ff6"),
    googleFontsBowerFont("italianno_regular", "italianno-bower", "Italianno-Regular.ttf", "147abbd3fe0e114880aa83786027c6f7a0c76b95"),
    googleFontsBowerFont("almendra_regular", "almendra-bower", "Almendra-Regular.ttf", "672d6ae6df2929fb6436fa3b32c5dae95d191d51"),
    googleFontsBowerFont("medievalsharp_regular", "medievalsharp-bower", "MedievalSharp.ttf", "4534f0e5d7fb1eb27e929052630edcf39af7855e"),
    googleFontsBowerFont("new_rocker_regular", "newrocker-bower", "NewRocker-Regular.ttf", "2b7993d3c19d303b4f05b06983479e415972f93a"),
    googleFontsBowerFont("metal_mania_regular", "metalmania-bower", "MetalMania-Regular.ttf", "db41a35fadc970e011bc076bba3f511160c80dde"),
    googleFontsBowerFont("fruktur_regular", "fruktur-bower", "Fruktur-Regular.ttf", "670f0848b151d387f0ee75c53bf5b01c8e52a120"),
    googleFontsBowerFont("fondamento_regular", "fondamento-bower", "Fondamento-Regular.ttf", "d0489b2934f853cb018a2e0c8ca3010ded0bb44c"),
    googleFontsBowerFont("im_fell_great_primer_regular", "imfellgreatprimer-bower", "IMFeGPrm28P.ttf", "5f7f32df83ce3d2120cf51222297de609774d298"),
    googleFontsBowerFont("marcellus_sc_regular", "marcellussc-bower", "MarcellusSC-Regular.ttf", "7304b969bf6ae3185f981c24960de897c47d2cd0"),
    googleFontsBowerFont("unna_regular", "unna-bower", "Unna-Regular.ttf", "2538f2c741aa7b6507646eb37c3a1a91fbbb182c"),
    googleFontsBowerFont("germania_one_regular", "germaniaone-bower", "GermaniaOne-Regular.ttf", "bd83f36e4de3b73028b0ab07b5129674fb4c5000"),
    googleFontsBowerFont("nixie_one_regular", "nixieone-bower", "NixieOne-Regular.ttf", "1bead886d30f7d5f094702279938f8b8f11f989f")
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
