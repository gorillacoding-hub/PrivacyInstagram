package com.feurstagram.extension;

import android.view.ViewGroup;

/**
 * GorillaInsta: development ADB debug bridge is permanently disabled.
 *
 * Upstream Feurstagram could register an exported BroadcastReceiver that read and
 * wrote settings, dumped UI trees, and forced update flows over ADB. That must not
 * ship in production.
 *
 * Public stubs remain so bytecode/call sites that reference these methods still link.
 * {@link #install} never registers a receiver. Network tracing stays off.
 */
public final class DebugBridge {

    public static final String TAG = "GorillaDebug";

    private DebugBridge() {}

    /** Always false — never log request paths or sensitive state. */
    public static boolean isNetworkTraceEnabled() {
        return false;
    }

    /** Always null — never spoof an installed version for update testing. */
    public static String pretendInstalledVersion() {
        return null;
    }

    /** No-op: does not register any BroadcastReceiver or export any interface. */
    public static void install(ViewGroup tabBar) {
        // Intentionally empty. Production builds must not expose a debug bridge.
    }
}
