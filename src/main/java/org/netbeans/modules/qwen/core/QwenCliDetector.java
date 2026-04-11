package org.netbeans.modules.qwen.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Detects whether qwen-code-cli and Node.js are available on the system PATH.
 */
public final class QwenCliDetector {

    private static final Logger LOG = Logger.getLogger(QwenCliDetector.class.getName());
    private static final String DEFAULT_CLI = "qwen-code";

    private QwenCliDetector() {}

    public static boolean isQwenAvailable(String customPath) {
        String exe = (customPath != null && !customPath.isEmpty()) ? customPath : DEFAULT_CLI;
        return probe(exe, "--version");
    }

    public static boolean isNodeAvailable() {
        return probe("node", "--version");
    }

    public static String resolvePath(String customPath) {
        if (customPath != null && !customPath.isEmpty()) return customPath;
        for (String name : new String[]{"qwen-code", "qwen-code-cli", "qwen"}) {
            if (probe(name, "--version")) return name;
        }
        return DEFAULT_CLI;
    }

    private static boolean probe(String executable, String arg) {
        try {
            ProcessBuilder pb = new ProcessBuilder(executable, arg);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            boolean ok = p.waitFor(10, TimeUnit.SECONDS);
            if (!ok) { p.destroyForcibly(); return false; }
            int exit = p.exitValue();
            if (exit == 0 || exit == 1) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line = r.readLine();
                    if (line != null && !line.isEmpty()) {
                        LOG.log(Level.FINE, "Detected {0}: {1}", new Object[]{executable, line});
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException | InterruptedException e) {
            LOG.log(Level.FINE, "Not found: " + executable, e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static String getDetectionMessage(String cliPath) {
        if (!isNodeAvailable()) return Bundle.msg_node_not_found();
        return Bundle.msg_cli_not_found();
    }
}
