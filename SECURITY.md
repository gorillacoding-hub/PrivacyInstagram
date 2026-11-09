# Security Policy

GorillaInsta is an unofficial open-source **patch toolkit** for the Instagram Android app
(forked from Feurstagram). It does not ship Instagram binaries.

## Trust model

- No collection, proxying, or transmission of Instagram credentials by this toolkit.
- No automatic download or installation of APKs at runtime.
- No production debug broadcast receiver.
- Users must supply and verify their own Instagram APK when building.

## Report

Report issues that involve credential leakage, unexpected fork-initiated network
requests, malicious patch behaviour, or unsafe build scripts. Prefer private contact
for sensitive details.

Do not report general Instagram bugs unrelated to these patches.
