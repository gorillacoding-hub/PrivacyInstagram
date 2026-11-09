package com.feurstagram.patches.debug

import app.morphe.patcher.patch.bytecodePatch
import com.feurstagram.patches.shared.Constants.COMPATIBILITY_INSTAGRAM

/**
 * GorillaInsta: debug bridge permanently disabled (default = false).
 * Even if someone enables this patch name, DebugBridge.install is a no-op and
 * never registers an exported receiver.
 */
@Suppress("unused")
val debugBridgePatch = bytecodePatch(
    name = "Debug bridge",
    description = "GorillaInsta: disabled. Does not expose ADB settings control or UI dumps.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_INSTAGRAM)

    execute {
        // No injection. Production builds must not register a debug receiver.
    }
}
