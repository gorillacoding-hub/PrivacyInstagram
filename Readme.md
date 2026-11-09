# GorillaInsta

Privacy-focused, minimal **patch toolkit** for the official Instagram Android app.

Based on [Feurstagram](https://github.com/jean-voila/FeurStagram) (GPL-3.0).  
**This repository is the open-source patch layer only.** It does not ship Instagram’s proprietary code.

## Goals

1. User privacy  
2. No automatic APK update / install path  
3. No production debug bridge  
4. No promotional prompts  
5. Keep Feurstagram’s network blocks (feed, Reels, Explore, ads, telemetry)  
6. Monochrome black/white settings UI  
7. Local archive helpers (24h stories / 7d messages) — hooks not wired without verified Instagram fingerprints  

## What changed vs upstream Feurstagram

| Area | GorillaInsta |
|------|----------------|
| Auto-update + APK download + PackageInstaller | **Removed** |
| `REQUEST_INSTALL_PACKAGES` patch | **Disabled / no-op** |
| Debug ADB BroadcastReceiver | **Removed (install is no-op)** |
| Follow-us Instagram card | **Removed** |
| Settings theme | Pure black / white monochrome |
| Local archive store | Added (`archive/`), purge on entry |
| Media quality helper | Added (`media/`), no invented IG hooks |
| Branding | GorillaInsta in settings UI and docs |

## Build (local only)

Requirements: JDK 21, Android SDK, GitHub token for Morphe packages (`gpr.user` / `gpr.key`), a trusted Instagram APK (e.g. APKMirror arm64).

```sh
./build.sh path/to/instagram.apk
# optional: --clone  (side-by-side package id)
# --debug is ineffective: debug bridge is a no-op
```

Do **not** distribute the resulting APK if it contains Instagram’s copyrighted code unless you have a legal basis to do so.

## Security model

- No runtime download of executable code from developer servers  
- No exported debug receiver  
- Analytics/telemetry paths remain blocked by `Block.java` (always-on)  
- Archive data stays in app-private storage under `files/gorilla_archive/`  

See `docs/SECURITY_AUDIT.md` and `docs/DEPENDENCY_INVENTORY.md`.

## Limitations (honest)

- **Not a standalone Instagram client.** Requires patching a real Instagram APK.  
- **Story / DM auto-archive** requires version-specific bytecode hooks that are **not** present in this fork. The storage API is ready; injection points are not invented.  
- **HD download button** same limitation — helper chooses largest variant when callers supply dimensions.  
- **Calling / contact-upload removal** inside Instagram’s own UI is server/binary driven; this patch set does not declare those fork features and does not add contact upload. Upstream also does not implement contact upload.  
- **Permissions of the final APK** are dominated by Instagram’s own manifest. This toolkit only avoids *adding* `REQUEST_INSTALL_PACKAGES`.  

## License

GPL-3.0 (see `LICENSE`), with attribution to Feurstagram / jean-voila. See `NOTICE`.
