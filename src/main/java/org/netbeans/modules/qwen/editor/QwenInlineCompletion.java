package org.netbeans.modules.qwen.editor;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import org.netbeans.modules.qwen.core.QwenCliClient;
import org.netbeans.modules.qwen.core.QwenLogger;
import org.netbeans.modules.qwen.core.QwenPreferences;

/**
 * Copilot-like inline completion.
 * After 500ms idle, fetches suggestion from CLI and shows ghost text.
 * Tab = accept, Escape = dismiss.
 */
public final class QwenInlineCompletion {

    private static final Logger LOG = Logger.getLogger(QwenInlineCompletion.class.getName());
    private static final int DEBOUNCE_MS = 500;

    private static QwenInlineCompletion instance;

    private final QwenPreferences prefs;
    private final QwenCliClient client;
    private javax.swing.Timer debounceTimer;

    private JTextComponent editor;
    private JComponent ghostLabel;
    private String suggestion;
    private int offset;
    private volatile boolean fetching;

    public static synchronized QwenInlineCompletion getInstance() {
        if (instance == null) instance = new QwenInlineCompletion();
        return instance;
    }

    private QwenInlineCompletion() {
        prefs = new QwenPreferences();
        client = new QwenCliClient(prefs);
        debounceTimer = new javax.swing.Timer(DEBOUNCE_MS, e -> doFetch());
        debounceTimer.setRepeats(false);
    }

    /** Register on the focus tracking. Call once at startup. */
    public void register() {
        // Use KeyboardFocusManager to track focus changes
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addPropertyChangeListener("permanentFocusOwner", evt -> {
                Object comp = evt.getNewValue();
                if (comp instanceof JTextComponent) {
                    switchTo((JTextComponent) comp);
                }
            });
    }

    private void switchTo(JTextComponent newEditor) {
        dismiss();
        editor = newEditor;
        if (editor == null) return;

        // Caret → restart debounce
        editor.addCaretListener(e -> { dismiss(); restartDebounce(); });

        // Document changes → dismiss
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { dismiss(); }
            @Override public void removeUpdate(DocumentEvent e) { dismiss(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        // Key → Tab accept, Escape dismiss
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent ke) {
                if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_TAB && suggestion != null) {
                    ke.consume(); accept();
                } else if (ke.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE && suggestion != null) {
                    ke.consume(); dismiss();
                }
            }
        });
    }

    private void restartDebounce() {
        debounceTimer.restart();
    }

    private void doFetch() {
        if (editor == null || !editor.isShowing() || fetching) return;

        int caret = editor.getCaretPosition();
        String ctx = getContext(caret);
        if (ctx == null || ctx.trim().isEmpty()) return;

        fetching = true;
        final int savedCaret = caret;
        final StringBuilder resp = new StringBuilder();

        QwenLogger.getInstance().fine("Fetching completion at " + caret);

        client.executeAsync(
            "Complete the code. Return ONLY the next line. No explanations, no fences.",
            ctx,
            new QwenCliClient.Callback() {
                @Override public void onOutput(String line) { resp.append(line).append("\n"); }
                @Override public void onError(String err) { fetching = false; }
                @Override public void onComplete() {
                    fetching = false;
                    if (editor != null && editor.isShowing() && editor.getCaretPosition() == savedCaret) {
                        String s = extract(resp.toString());
                        if (s != null && !s.isEmpty()) {
                            SwingUtilities.invokeLater(() -> showGhost(s, savedCaret));
                        }
                    }
                }
            }
        );
    }

    private String extract(String resp) {
        // Try code block
        String cb = QwenDiffApplier.extractCodeBlock(resp);
        if (cb != null && !cb.isEmpty()) {
            for (String line : cb.split("\n")) {
                if (!line.trim().isEmpty()) return line.trim();
            }
        }
        // First meaningful line
        for (String line : resp.split("\n")) {
            String t = line.trim();
            if (!t.isEmpty() && !t.startsWith("[") && !t.startsWith("ERROR")
                    && !t.startsWith("Processing") && !t.startsWith("Executing")) {
                return t;
            }
        }
        return null;
    }

    private void showGhost(String text, int off) {
        suggestion = text;
        offset = off;

        JRootPane root = SwingUtilities.getRootPane(editor);
        if (root == null) return;
        JLayeredPane lp = root.getLayeredPane();

        if (ghostLabel == null) {
            ghostLabel = new GhostComponent();
        }

        ghostLabel.setFont(editor.getFont());
        ((GhostComponent) ghostLabel).setText(text);
        lp.add(ghostLabel, JLayeredPane.POPUP_LAYER);

        reposition();
    }

    private void reposition() {
        if (ghostLabel == null || editor == null || suggestion == null) return;
        try {
            Rectangle r = editor.modelToView(offset);
            if (r == null) return;
            JRootPane root = SwingUtilities.getRootPane(editor);
            if (root == null) return;
            JLayeredPane lp = root.getLayeredPane();
            Point p = SwingUtilities.convertPoint(editor, r.getLocation(), lp);
            ghostLabel.setBounds(p.x + r.width + 2, p.y, 300, r.height);
            ghostLabel.setVisible(true);
        } catch (BadLocationException e) { hideGhost(); }
    }

    private void hideGhost() {
        suggestion = null;
        if (ghostLabel != null) {
            Container parent = ghostLabel.getParent();
            if (parent != null) parent.remove(ghostLabel);
            ghostLabel = null;
        }
    }

    private void dismiss() {
        hideGhost();
        debounceTimer.stop();
    }

    private void accept() {
        if (suggestion == null || editor == null) return;
        try {
            editor.getDocument().insertString(offset, suggestion, null);
            editor.setCaretPosition(offset + suggestion.length());
        } catch (BadLocationException e) {
            QwenLogger.getInstance().log(Level.WARNING, "Accept failed", e);
        }
        dismiss();
    }

    private String getContext(int caret) {
        try {
            Document doc = editor.getDocument();
            int start = Math.max(0, caret - 500);
            return doc.getText(start, caret - start);
        } catch (BadLocationException e) { return null; }
    }

    // ---- Ghost rendering ----

    private class GhostComponent extends JComponent {
        private String text;

        GhostComponent() { setOpaque(false); }

        void setText(String t) { this.text = t; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            if (text == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(128, 128, 128, 150));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, 0, fm.getAscent());
            g2.dispose();
        }

        @Override public Dimension getPreferredSize() {
            if (text == null || getFont() == null) return new Dimension(0, 0);
            return new Dimension(getFontMetrics(getFont()).stringWidth(text), getFontMetrics(getFont()).getHeight());
        }
    }
}
