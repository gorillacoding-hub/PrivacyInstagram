package com.feurstagram.patches.update

import app.morphe.patcher.patch.resourcePatch
import com.feurstagram.patches.shared.Constants.COMPATIBILITY_INSTAGRAM

/**
 * GorillaInsta: REQUEST_INSTALL_PACKAGES is intentionally NOT declared.
 * Automatic APK download/install was removed; this permission must not reappear.
 */
@Suppress("unused")
val installPermissionPatch = resourcePatch(
    name = "Install-packages permission",
    description = "GorillaInsta: deliberately does not declare REQUEST_INSTALL_PACKAGES " +
        "(in-app updater removed).",
    default = false,
) {
    compatibleWith(COMPATIBILITY_INSTAGRAM)

    execute {
        // No-op: never add REQUEST_INSTALL_PACKAGES.
    }
}
