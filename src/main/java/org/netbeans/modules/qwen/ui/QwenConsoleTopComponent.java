package org.netbeans.modules.qwen.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.netbeans.modules.qwen.core.QwenCliClient;
import org.netbeans.modules.qwen.core.QwenPreferences;
import org.netbeans.modules.qwen.core.QwenSession;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Qwen AI Console — interactive console + log viewer.
 * Shows all CLI output, errors, and plugin activity.
 * Access: Qwen → Qwen: Open Console OR Window → Qwen: Open Console
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

    private JTextArea logArea;
    private JTextField inputField;
    private QwenSession session;
    private QwenPreferences prefs;
    private QwenCliClient client;

    public QwenConsoleTopComponent() {
        initComponents();
        setName("Qwen AI Console");
        setToolTipText("Qwen AI interactive console and log viewer");

        session = new QwenSession();
        prefs = new QwenPreferences();
        client = new QwenCliClient(prefs);

        append("=== Qwen AI Console ===");
        append("Plugin initialized successfully.");
        append("Session: " + session.getId());
        append("");
        append("Commands: /help, /clear, /stats, /compress");
        append("Any other text is sent to qwen-code-cli.");
        append("");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputField = new JTextField();
        inputField.setToolTipText("Type a message... (/help, /clear, /stats, /compress)");
        inputField.addActionListener(e -> sendInput());
        inputPanel.add(inputField, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton btnSend = new JButton("Send");
        JButton btnClear = new JButton("Clear");
        JButton btnStats = new JButton("/stats");

        btnSend.addActionListener(e -> sendInput());
        btnClear.addActionListener(e -> logArea.setText(""));
        btnStats.addActionListener(e -> append(session.stats()));

        btns.add(btnSend);
        btns.add(btnStats);
        btns.add(btnClear);
        inputPanel.add(btns, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendInput() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;
        inputField.setText("");
        append("> " + input);

        if (input.equals("/clear")) { session.clear(); append("[Session cleared]"); return; }
        if (input.equals("/stats")) { append(session.stats()); return; }
        if (input.equals("/compress")) { append("[Context compressed]"); session.clear(); return; }
        if (input.equals("/help")) {
            append("Commands: /clear, /stats, /compress, /help");
            append("Any other text → sent to qwen-code-cli");
            return;
        }

        // Send to CLI
        session.setActive(true);
        append("[Sending to CLI...]");
        client.executeAsync(input, null, new QwenCliClient.Callback() {
            @Override public void onOutput(String line) { append(line); }
            @Override public void onError(String error) { append("[ERROR] " + error); }
            @Override public void onComplete() { append("\n[Done]"); }
        });
    }

    private void append(String line) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
}
