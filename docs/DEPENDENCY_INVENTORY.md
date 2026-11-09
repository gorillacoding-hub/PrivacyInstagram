# Dependency inventory (direct)

From `gradle/libs.versions.toml` and project scripts:

| Coordinate | Version | Purpose | Notes |
|------------|---------|---------|-------|
| `app.morphe:morphe-patches-library` | 1.2.0 | Patch helpers | GitHub Packages (MorpheApp) |
| `app.morphe.patches` plugin | 1.3.2 | Patch project plugin | settings.gradle.kts |
| `app.morphe:morphe-patcher` (version catalog) | 1.5.2 | Patcher runtime (CLI) | Build host |
| `com.google.code.gson:gson` | 2.14.0 | JSON | Catalogued; confirm usage before shipping |
| smali (`d92701d947`) | ref pin | Bytecode | Catalogued |

Transitive dependencies are resolved by Gradle at build time. Generate an SBOM locally:

```sh
./gradlew :patches:dependencies > dependencies-full.txt
```

## Binary tools (build.sh)

Upstream `build.sh` expects JARs under `tools/` (Morphe CLI, APKEditor, etc.).  
**Do not execute unpinned scripts downloaded at build time.** Verify checksums of any tool JARs you place in `tools/`.

## Not included

- Instagram APK  
- Google Play services extras beyond what Instagram already embeds  
- Analytics SDKs added by this fork  

