package com.feurstagram.extension;

import android.content.Context;
import android.widget.Toast;

/**
 * GorillaInsta: automatic update checks, APK download, and in-app install are
 * intentionally removed.
 *
 * Upstream Feurstagram contacted GitHub, downloaded an APK, and staged it through
 * PackageInstaller (REQUEST_INSTALL_PACKAGES). That is a remote-code / supply-chain
 * surface. GorillaInsta never downloads or installs executable code at runtime.
 *
 * Users who want a newer build must rebuild from source against a trusted Instagram APK.
 *
 * Public method names are kept so existing call sites compile without change.
 */
public final class UpdateChecker {

    private UpdateChecker() {}

    /** No-op: launch-time update check disabled permanently. */
    public static void check(Context context) {
        // Intentionally empty.
    }

    /** Informational only — does not contact the network or download anything. */
    public static void checkNow(Context context) {
        if (context == null) return;
        try {
            Toast.makeText(
                    context.getApplicationContext(),
                    "GorillaInsta: automatic updates removed. Rebuild from source for new versions.",
                    Toast.LENGTH_LONG
            ).show();
        } catch (Throwable ignored) {
            // UI may be unavailable; never fail the host app for this.
        }
    }

    /** No-op: release notes popup removed with the updater. */
    public static void checkWhatsNew(Context context) {
        // Intentionally empty.
    }

    /** No-op: release notes popup removed with the updater. */
    public static void showLatestNotes(Context context) {
        // Intentionally empty.
    }
}
