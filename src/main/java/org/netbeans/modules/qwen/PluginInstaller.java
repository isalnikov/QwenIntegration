package org.netbeans.modules.qwen;

import org.netbeans.modules.qwen.core.Bundle;
import org.netbeans.modules.qwen.core.QwenCliDetector;
import org.netbeans.modules.qwen.core.QwenLogger;
import org.netbeans.modules.qwen.core.QwenPreferences;
import org.netbeans.modules.qwen.editor.QwenInlineCompletion;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;

/**
 * Module installer. Runs once when the plugin is loaded.
 * 1. Registers inline completion engine
 * 2. Checks for CLI availability
 */
public class PluginInstaller extends ModuleInstall {

    private static final RequestProcessor RP = new RequestProcessor("QwenInit", 1);
    private static volatile QwenPreferences prefs;

    @Override
    public void restored() {
        // Lazy singleton
        getPrefs();

        QwenLogger.getInstance().info("Plugin starting");

        // Register inline completion (Copilot-like)
        try {
            QwenInlineCompletion.getInstance().register();
            QwenLogger.getInstance().info("Inline completion registered");
        } catch (Exception e) {
            QwenLogger.getInstance().log(java.util.logging.Level.SEVERE, "Inline completion failed", e);
        }

        // Check CLI in background
        RP.execute(() -> {
            String path = getPrefs().getCliPath();
            if (!QwenCliDetector.isQwenAvailable(path)) {
                String msg = QwenCliDetector.getDetectionMessage(path) +
                             "\n\nInstall: npm install -g @qwen-code/qwen-code\nOr: ollama pull qwen:code";
                java.awt.EventQueue.invokeLater(() -> {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                });
            }
        });
    }

    public static QwenPreferences getPrefs() {
        if (prefs == null) {
            synchronized (PluginInstaller.class) {
                if (prefs == null) prefs = new QwenPreferences();
            }
        }
        return prefs;
    }
}
