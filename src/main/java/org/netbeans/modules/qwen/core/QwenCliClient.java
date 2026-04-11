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

/** Executes qwen-code-cli commands (sync + async). */
public final class QwenCliClient {
    private static final Logger LOG = Logger.getLogger(QwenCliClient.class.getName());
    private static final RequestProcessor RP = new RequestProcessor("QwenCli", 3);
    private final QwenPreferences prefs;

    public interface Callback {
        void onOutput(String line);
        void onError(String error);
        void onComplete();
    }

    public QwenCliClient(QwenPreferences prefs) { this.prefs = prefs; }

    /** Async execution with streaming callbacks. */
    public void executeAsync(String prompt, String codeContext, Callback cb) {
        RP.execute(() -> {
            try {
                String cli = prefs.getEffectiveCliPath();
                String model = prefs.getModel();
                int timeout = prefs.getTimeout();
                ProcessBuilder pb = new ProcessBuilder(cli);
                if (model != null && !model.isEmpty()) { pb.command().add("--model"); pb.command().add(model); }

                LOG.log(Level.INFO, "Async executing: {0}", pb.command());
                Process p = pb.start();

                String input = buildInput(prompt, codeContext);
                try (OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
                    w.write(input); w.flush();
                }

                try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = r.readLine()) != null) { if (cb != null) cb.onOutput(line); }
                }

                boolean done = p.waitFor(timeout, TimeUnit.SECONDS);
                if (!done) { p.destroyForcibly(); if (cb != null) cb.onError("Timeout " + timeout + "s"); return; }
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
        sb.append("You are an AI coding assistant in an IDE.\n\n");
        if (codeContext != null && !codeContext.isEmpty())
            sb.append("Code:\n```\n").append(codeContext).append("\n```\n\n");
        sb.append("Request: ").append(prompt).append("\n");
        return sb.toString();
    }
}
