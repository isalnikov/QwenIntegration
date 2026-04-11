package org.netbeans.modules.qwen.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Detects qwen-code-cli and Node.js on PATH. */
public final class QwenCliDetector {
    private static final Logger LOG = Logger.getLogger(QwenCliDetector.class.getName());
    private QwenCliDetector() {}

    public static boolean isQwenAvailable(String customPath) {
        String exe = (customPath != null && !customPath.isEmpty()) ? customPath : "qwen-code";
        return probe(exe);
    }

    public static boolean isNodeAvailable() { return probe("node"); }

    public static String resolvePath(String customPath) {
        if (customPath != null && !customPath.isEmpty()) return customPath;
        for (String n : new String[]{"qwen-code", "qwen-code-cli", "qwen"})
            if (probe(n)) return n;
        return "qwen-code";
    }

    private static boolean probe(String exe) {
        try {
            ProcessBuilder pb = new ProcessBuilder(exe, "--version");
            pb.redirectErrorStream(true);
            Process p = pb.start();
            boolean ok = p.waitFor(10, TimeUnit.SECONDS);
            if (!ok) { p.destroyForcibly(); return false; }
            int exit = p.exitValue();
            if (exit == 0 || exit == 1) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line = r.readLine();
                    if (line != null && !line.isEmpty()) {
                        LOG.log(Level.FINE, "Detected {0}: {1}", new Object[]{exe, line});
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException | InterruptedException e) {
            LOG.log(Level.FINE, "Not found: " + exe, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
