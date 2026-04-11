package org.netbeans.modules.qwen.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.RequestProcessor;

/**
 * Executes qwen-code-cli commands. Sync and async.
 */
public final class QwenCliClient {

    private static final Logger LOG = Logger.getLogger(QwenCliClient.class.getName());
    private static final RequestProcessor RP = new RequestProcessor("QwenCli", 3);

    private final QwenPreferences prefs;

    public interface Callback {
        void onOutput(String line);
        void onError(String error);
        void onComplete();
    }

    public QwenCliClient(QwenPreferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Synchronous execution. Returns full stdout.
     */
    public String executeSync(String prompt, String codeContext) throws IOException, InterruptedException {
        String cli = prefs.getEffectiveCliPath();
        String model = prefs.getModel();
        int timeout = prefs.getTimeout();

        ProcessBuilder pb = new ProcessBuilder(cli);
        if (model != null && !model.isEmpty()) {
            pb.command().add("--model");
            pb.command().add(model);
        }

        LOG.log(Level.INFO, "Executing: {0}", pb.command());
        Process p = pb.start();

        // Write input
        String input = buildInput(prompt, codeContext);
        try (OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
            w.write(input);
            w.flush();
        }

        // Read output
        StringBuilder out = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) out.append(line).append("\n");
        }

        boolean done = p.waitFor(timeout, TimeUnit.SECONDS);
        if (!done) {
            p.destroyForcibly();
            throw new IOException("Timeout after " + timeout + "s");
        }

        int exit = p.exitValue();
        if (exit != 0) {
            StringBuilder err = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) err.append(line).append("\n");
            }
            throw new IOException("Exit " + exit + ": " + err.substring(0, Math.min(200, err.length())));
        }

        LOG.log(Level.INFO, "Response: {0} chars", out.length());
        return out.toString().trim();
    }

    /**
     * Asynchronous execution with streaming callbacks.
     */
    public void executeAsync(String prompt, String codeContext, Callback cb) {
        RP.execute(() -> {
            try {
                String cli = prefs.getEffectiveCliPath();
                String model = prefs.getModel();
                int timeout = prefs.getTimeout();

                ProcessBuilder pb = new ProcessBuilder(cli);
                if (model != null && !model.isEmpty()) {
                    pb.command().add("--model");
                    pb.command().add(model);
                }

                LOG.log(Level.INFO, "Async executing: {0}", pb.command());
                Process p = pb.start();

                String input = buildInput(prompt, codeContext);
                try (OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
                    w.write(input);
                    w.flush();
                }

                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        if (cb != null) cb.onOutput(line);
                    }
                }

                boolean done = p.waitFor(timeout, TimeUnit.SECONDS);
                if (!done) {
                    p.destroyForcibly();
                    if (cb != null) cb.onError("Timeout after " + timeout + "s");
                    return;
                }

                if (cb != null) cb.onComplete();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (cb != null) cb.onError("Interrupted");
            } catch (IOException e) {
                if (cb != null) cb.onError(e.getMessage());
            }
        });
    }

    private String buildInput(String prompt, String codeContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI coding assistant in an IDE. Provide clear, concise responses.\n\n");
        if (codeContext != null && !codeContext.isEmpty()) {
            sb.append("Code context:\n```\n").append(codeContext).append("\n```\n\n");
        }
        sb.append("Request: ").append(prompt).append("\n");
        return sb.toString();
    }
}
