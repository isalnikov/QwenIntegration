package org.netbeans.modules.qwen.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.netbeans.modules.qwen.core.Bundle;
import org.netbeans.modules.qwen.core.QwenCliClient;
import org.netbeans.modules.qwen.core.QwenLogger;
import org.netbeans.modules.qwen.core.QwenLogger.ConsoleSink;
import org.netbeans.modules.qwen.core.QwenPreferences;
import org.netbeans.modules.qwen.core.QwenSession;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.NbBundle.Messages;

/**
 * Qwen AI Console — interactive console + log viewer.
 * Shows all CLI output, errors, and plugin activity logs.
 */
@TopComponent.Description(
    preferredID = "QwenConsoleTopComponent",
    persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@Messages({
    "CTL_QwenConsoleAction=Qwen AI Console",
    "CTL_QwenConsoleTopComponent=Qwen AI Console"
})
public final class QwenConsoleTopComponent extends TopComponent {

    private static final String PREFERRED_ID = "QwenConsoleTopComponent";

    private JTextArea logArea;
    private JTextField inputField;

    private transient QwenSession session;
    private transient QwenPreferences prefs;
    private transient QwenCliClient client;

    public QwenConsoleTopComponent() {
        initComponents();
        setName(Bundle.console_title());
        setToolTipText("Qwen AI Console");

        session = new QwenSession();
        prefs = new QwenPreferences();
        client = new QwenCliClient(prefs);

        // Register as log sink
        QwenLogger.getInstance().setSink(line -> appendLog(line));

        appendLog("=== " + Bundle.console_title() + " ===");
        appendLog(Bundle.msg_welcome());
        appendLog("Session: " + session.getId());
        appendLog("");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputField = new JTextField();
        inputField.setToolTipText(Bundle.console_placeholder());
        inputField.addActionListener(e -> sendInput());
        inputPanel.add(inputField, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnSend = new JButton(Bundle.console_send());
        JButton btnClear = new JButton(Bundle.console_clear());
        JButton btnStats = new JButton("/stats");
        JButton btnCompress = new JButton("/compress");

        btnSend.addActionListener(e -> sendInput());
        btnClear.addActionListener(e -> logArea.setText(""));
        btnStats.addActionListener(e -> appendLog(session.stats()));
        btnCompress.addActionListener(e -> appendLog("Context compressed (local reset)"));

        buttons.add(btnSend);
        buttons.add(btnStats);
        buttons.add(btnCompress);
        buttons.add(btnClear);
        inputPanel.add(buttons, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;
        inputField.setText("");

        appendLog("> " + input);

        // Local commands
        if (input.equals("/clear")) {
            session.clear();
            appendLog("[Session cleared]");
            return;
        }
        if (input.equals("/stats")) {
            appendLog(session.stats());
            return;
        }
        if (input.equals("/compress")) {
            appendLog("[Context compressed]");
            session.clear();
            return;
        }
        if (input.equals("/help")) {
            appendLog("Commands: /clear, /stats, /compress, /help");
            appendLog("Any other text is sent to qwen-code-cli.");
            return;
        }

        // Send to CLI
        session.setActive(true);
        QwenLogger.getInstance().info("Console input: " + input);
        client.executeAsync(input, null, new QwenCliClient.Callback() {
            @Override public void onOutput(String line) {
                appendLog(line);
            }
            @Override public void onError(String error) {
                appendLog("[ERROR] " + error);
            }
            @Override public void onComplete() {
                appendLog("\n[Done]");
            }
        });
    }

    private void appendLog(String line) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static QwenConsoleTopComponent findInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (tc == null) {
            return new QwenConsoleTopComponent();
        }
        return (QwenConsoleTopComponent) tc;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
}
