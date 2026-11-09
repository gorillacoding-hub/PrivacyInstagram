group = "com.gorillainsta"

patches {
    about {
        name = "GorillaInsta Patches"
        description = "Privacy-focused Instagram patch fork: removes auto-update/debug surfaces, " +
            "keeps network blocks for feed/reels/ads/telemetry, monochrome settings UI."
        source = "local"
        author = "GorillaInsta"
        contact = "na"
        website = "na"
        license = "GPLv3"
    }
}

dependencies {
    // Provides app.morphe.util.* helpers (DOM, bytecode, references) used by the patches.
    implementation(libs.morphe.patches.library)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
