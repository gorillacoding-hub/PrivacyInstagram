package com.feurstagram.extension.archive;

import android.content.Context;

import com.feurstagram.extension.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * App-private local archive store for GorillaInsta.
 *
 * Design:
 * - Files live under {@code context.getFilesDir()/gorilla_archive/...} (MODE_PRIVATE).
 * - Metadata uses explicit expiration timestamps (not file mtime).
 * - Paths are validated against the archive root (no traversal).
 * - Writes are atomic (temp file + rename) where practical.
 * - No network I/O. No analytics. Content is never logged.
 *
 * IMPORTANT LIMITATION:
 * This class only stores bytes the caller already has. Feurstagram / GorillaInsta
 * does not yet include verified Instagram bytecode hooks that intercept every
 * received story or DM attachment. Without those hooks, automatic archival of
 * stories/messages cannot run. Wire hooks only after fingerprinting a specific
 * Instagram version — do not invent class names.
 */
public final class LocalArchiveStore {

    public static final long STORY_TTL_MS = 24L * 60L * 60L * 1000L;
    public static final long MESSAGE_TTL_MS = 7L * 24L * 60L * 60L * 1000L;

    private static final String ROOT = "gorilla_archive";
    private static final String STORIES = "stories";
    private static final String MESSAGES = "messages";
    private static final String META_SUFFIX = ".meta";

    private LocalArchiveStore() {}

    public static File root(Context context) {
        return new File(context.getFilesDir(), ROOT);
    }

    public static File storiesDir(Context context) {
        File d = new File(root(context), STORIES);
        //noinspection ResultOfMethodCallIgnored
        d.mkdirs();
        return d;
    }

    public static File messagesDir(Context context) {
        File d = new File(root(context), MESSAGES);
        //noinspection ResultOfMethodCallIgnored
        d.mkdirs();
        return d;
    }

    /**
     * Store raw bytes under the stories archive with a 24h expiration.
     * Returns the stored file or null on failure.
     */
    public static File putStory(Context context, String id, byte[] data, String mimeHint) {
        return put(context, storiesDir(context), id, data, mimeHint, STORY_TTL_MS);
    }

    /**
     * Store raw bytes under the messages archive with a 7-day expiration.
     */
    public static File putMessage(Context context, String id, byte[] data, String mimeHint) {
        return put(context, messagesDir(context), id, data, mimeHint, MESSAGE_TTL_MS);
    }

    private static File put(Context context, File dir, String id, byte[] data, String mimeHint, long ttlMs) {
        if (context == null || data == null || data.length == 0) return null;
        String safe = sanitizeId(id);
        if (safe == null) return null;
        try {
            String ext = extensionFor(mimeHint);
            File target = resolveInside(dir, safe + ext);
            if (target == null) return null;
            File tmp = new File(target.getParentFile(), target.getName() + ".tmp");
            try (FileOutputStream out = new FileOutputStream(tmp)) {
                out.write(data);
                out.getFD().sync();
            }
            if (target.exists() && !target.delete()) {
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
                return null;
            }
            if (!tmp.renameTo(target)) {
                //noinspection ResultOfMethodCallIgnored
                tmp.delete();
                return null;
            }
            long expires = System.currentTimeMillis() + ttlMs;
            writeMeta(new File(target.getAbsolutePath() + META_SUFFIX), expires, mimeHint, data.length);
            return target;
        } catch (IOException e) {
            return null;
        }
    }

    /** Delete expired story and message entries. Safe to call on startup. */
    public static int purgeExpired(Context context) {
        if (context == null) return 0;
        int n = 0;
        n += purgeDir(storiesDir(context));
        n += purgeDir(messagesDir(context));
        return n;
    }

    private static int purgeDir(File dir) {
        if (dir == null || !dir.isDirectory()) return 0;
        int n = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        long now = System.currentTimeMillis();
        for (File f : files) {
            if (f.getName().endsWith(META_SUFFIX)) continue;
            File meta = new File(f.getAbsolutePath() + META_SUFFIX);
            long exp = readExpires(meta);
            if (exp > 0L && exp <= now) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
                //noinspection ResultOfMethodCallIgnored
                meta.delete();
                n++;
            }
        }
        return n;
    }

    public static List<File> listActive(Context context, boolean stories) {
        List<File> out = new ArrayList<>();
        File dir = stories ? storiesDir(context) : messagesDir(context);
        File[] files = dir.listFiles();
        if (files == null) return out;
        long now = System.currentTimeMillis();
        for (File f : files) {
            if (f.getName().endsWith(META_SUFFIX)) continue;
            long exp = readExpires(new File(f.getAbsolutePath() + META_SUFFIX));
            if (exp == 0L || exp > now) out.add(f);
        }
        return out;
    }

    private static void writeMeta(File meta, long expiresAt, String mime, int size) throws IOException {
        String body = "expires=" + expiresAt + "\nmime=" + nullToEmpty(mime) + "\nsize=" + size + "\n";
        File tmp = new File(meta.getAbsolutePath() + ".tmp");
        try (FileOutputStream out = new FileOutputStream(tmp)) {
            out.write(body.getBytes(StandardCharsets.UTF_8));
            out.getFD().sync();
        }
        if (meta.exists()) {
            //noinspection ResultOfMethodCallIgnored
            meta.delete();
        }
        if (!tmp.renameTo(meta)) {
            //noinspection ResultOfMethodCallIgnored
            tmp.delete();
            throw new IOException("meta rename failed");
        }
    }

    private static long readExpires(File meta) {
        if (meta == null || !meta.isFile()) return 0L;
        try (FileInputStream in = new FileInputStream(meta)) {
            byte[] buf = new byte[256];
            int n = in.read(buf);
            if (n <= 0) return 0L;
            String s = new String(buf, 0, n, StandardCharsets.UTF_8);
            for (String line : s.split("\n")) {
                if (line.startsWith("expires=")) {
                    return Long.parseLong(line.substring("expires=".length()).trim());
                }
            }
        } catch (Exception ignored) {
            return 0L;
        }
        return 0L;
    }

    private static String sanitizeId(String id) {
        if (id == null || id.isEmpty()) return null;
        // Prefer a stable hash so arbitrary Instagram ids cannot introduce path chars.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(id.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (int i = 0; i < 16; i++) sb.append(String.format(Locale.US, "%02x", dig[i]));
            return sb.toString();
        } catch (Exception e) {
            String cleaned = id.replaceAll("[^A-Za-z0-9._-]", "_");
            if (cleaned.isEmpty() || cleaned.contains("..")) return null;
            return cleaned.length() > 64 ? cleaned.substring(0, 64) : cleaned;
        }
    }

    private static File resolveInside(File dir, String name) {
        try {
            File f = new File(dir, name);
            String rootPath = dir.getCanonicalPath();
            String filePath = f.getCanonicalPath();
            if (!filePath.startsWith(rootPath + File.separator) && !filePath.equals(rootPath)) {
                return null;
            }
            return f;
        } catch (IOException e) {
            return null;
        }
    }

    private static String extensionFor(String mime) {
        if (mime == null) return ".bin";
        String m = mime.toLowerCase(Locale.US);
        if (m.contains("jpeg") || m.contains("jpg")) return ".jpg";
        if (m.contains("png")) return ".png";
        if (m.contains("webp")) return ".webp";
        if (m.contains("mp4")) return ".mp4";
        if (m.contains("webm")) return ".webm";
        if (m.contains("text")) return ".txt";
        return ".bin";
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
