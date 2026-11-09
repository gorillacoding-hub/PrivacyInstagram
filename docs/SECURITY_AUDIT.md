# GorillaInsta Security Audit Report

Date: 2026-11-09  
Base: Feurstagram (jean-voila/FeurStagram) patch toolkit  

## Threat model (prioritized)

| Severity | Threat | Mitigation in GorillaInsta |
|----------|--------|----------------------------|
| Critical | In-app APK download + PackageInstaller (remote executable) | `UpdateChecker` stubbed; no HTTP download; no installer session |
| Critical | Exported debug BroadcastReceiver controlling settings | `DebugBridge.install` no-op; patch does not inject |
| High | `REQUEST_INSTALL_PACKAGES` | Install-permission patch is no-op / default false |
| High | Developer update channel as supply chain | Auto-update forced off; manual rebuild only |
| Medium | Unnecessary third-party network from fork | Follow prompt removed; update API calls removed |
| Medium | Sensitive logging | Network trace always false; no archive content logging |
| Medium | Path traversal in local archive | Canonical-path check + hashed ids |
| Low | Promotional surface | FollowPrompt no-op |
| Inherited | Instagram’s own telemetry remaining paths | `Block.java` always blocks analytics/commerce families |
| Inherited | Instagram binary permissions | Cannot strip Meta’s manifest from patch-only toolkit without full resource rewrite per version |

## Category checklist (required)

| Category | Result |
|----------|--------|
| Debug interfaces | Removed (no-op + non-injecting patch) |
| Automatic updater | Removed |
| Remote code execution (fork-controlled) | No unauthorized download/exec path in extension |
| Supply-chain (project scripts) | Gradle deps pinned in `libs.versions.toml`; Morphe from GitHub Packages (needs token); user must verify Instagram APK hash themselves |
| Analytics and trackers (fork) | No fork analytics; IG telemetry blocked at network layer where listed |
| IMEI/SIM access (fork code) | None in extension sources |
| Contact uploading (fork code) | None present; not added |
| Unnecessary permissions (added by fork) | Install-packages not added |
| Calling features | Not introduced by fork; deep IG call UI not safely removable here |
| Monochrome theme | Settings palette pure black / white |
| Media downloads | Helper only; no fake “original quality” claim |
| Story archive | Storage + 24h TTL ready; **hooks not wired** |
| Message archive | Storage + 7d TTL ready; **hooks not wired** |
| Automatic cleanup | `ArchiveMaintenance.runOnce` purges expired files |
| Build | Patch sources prepared; full APK build needs user’s Instagram APK + Morphe token |
| Final APK | **Not produced** (would embed Meta proprietary code) |

## Network endpoint inventory (fork-controlled)

| Endpoint | Status |
|----------|--------|
| `api.github.com/repos/.../releases` (upstream updater) | **Removed** from runtime |
| GitHub release APK CDN (upstream updater) | **Removed** |
| Instagram / Meta hosts | Required for core app; blocked subsets in `Block.java` (ads, suggested, analytics, commerce) |

## Unresolved / inherited

1. Full merged-manifest permission minimization needs APK resource patching of Instagram’s own declarations — out of scope without per-version analysis of which permissions break login/media.  
2. Story/DM interception hooks not implemented (no verified fingerprints in this repo).  
3. Gradle still pulls Morphe patcher from GitHub Packages (upstream design).  
4. Signature-check bypass remains (required for deep links on re-signed builds) — document as intentional compatibility patch, not a “backdoor” to remote code.  

## Removed components (list)

- Functional `UpdateChecker` download/install pipeline  
- Functional `DebugBridge` exported receiver actions  
- `InstallPermissionPatch` permission injection  
- `FollowPrompt` promotional UI  

