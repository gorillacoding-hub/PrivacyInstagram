package com.feurstagram.extension;

import android.content.Context;

/**
 * GorillaInsta: promotional "follow our Instagram account" card removed.
 * Upstream showed a follow prompt after install/update; that is not appropriate
 * for a privacy-focused build.
 */
public final class FollowPrompt {

    private FollowPrompt() {}

    /** Always continues without showing a card. */
    public static void maybeShow(final Context context, final Runnable then) {
        if (then != null) then.run();
    }
}
