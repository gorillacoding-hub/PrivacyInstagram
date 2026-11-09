package com.feurstagram.extension.media;

/**
 * Guidance and helpers for highest-available-quality media saves.
 *
 * Instagram serves multiple quality variants. Prefer the largest non-transcoded
 * candidate the client already resolved (width × height, then bitrate for video).
 *
 * LIMITATION: Feurstagram/GorillaInsta does not currently inject a media-download
 * button via a verified bytecode fingerprint. Implementing that requires mapping
 * Instagram's media model fields for a specific app version. Do not invent
 * class/method names. When a verified hook exists, feed the chosen URL/bytes
 * into app-private storage or the system photo picker / SAF — avoid broad
 * READ/WRITE_EXTERNAL_STORAGE where possible.
 */
public final class HighQualityDownload {

    private HighQualityDownload() {}

    /**
     * Pick the best candidate among width/height pairs. Returns the index of the
     * largest area; ties prefer the later index (often higher bitrate lists).
     */
    public static int pickLargest(int[] widths, int[] heights) {
        if (widths == null || heights == null || widths.length == 0) return -1;
        int n = Math.min(widths.length, heights.length);
        int best = 0;
        long bestArea = -1L;
        for (int i = 0; i < n; i++) {
            long area = (long) Math.max(0, widths[i]) * (long) Math.max(0, heights[i]);
            if (area >= bestArea) {
                bestArea = area;
                best = i;
            }
        }
        return best;
    }
}
