package com.feurstagram.extension.archive;

import android.content.Context;

import com.feurstagram.extension.Config;

/**
 * Startup cleanup for local archives. Call once when settings/entry hooks run.
 * Does not touch Instagram content caches outside gorilla_archive/.
 */
public final class ArchiveMaintenance {

    private static boolean ran;

    private ArchiveMaintenance() {}

    public static void runOnce() {
        if (ran) return;
        ran = true;
        Context ctx = Config.getAppContext();
        if (ctx == null) return;
        try {
            LocalArchiveStore.purgeExpired(ctx);
        } catch (Throwable ignored) {
            // Never crash the host for archive maintenance.
        }
    }
}
