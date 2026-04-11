package org.netbeans.modules.qwen.editor;

import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Applies code from Qwen responses to the active editor.
 */
public final class QwenDiffApplier {

    private static final Logger LOG = Logger.getLogger(QwenDiffApplier.class.getName());

    public enum Result { APPLIED, PARSE_ERROR, NO_EDITOR }

    public static Result apply(String response) {
        if (response == null || response.isEmpty()) return Result.PARSE_ERROR;

        JTextComponent editor = getActiveEditor();
        if (editor == null) return Result.NO_EDITOR;

        String code = extractCodeBlock(response);
        if (code == null) code = response;

        try {
            int s = editor.getSelectionStart();
            int e = editor.getSelectionEnd();
            if (s != e) {
                editor.getDocument().remove(s, e - s);
                editor.getDocument().insertString(s, code, null);
            } else {
                editor.getDocument().insertString(editor.getCaretPosition(), code + "\n", null);
            }
            return Result.APPLIED;
        } catch (BadLocationException ex) {
            LOG.warning("Apply failed: " + ex.getMessage());
            return Result.PARSE_ERROR;
        }
    }

    /** Extract content between ``` fences. Returns null if not found. */
    public static String extractCodeBlock(String text) {
        if (text == null) return null;
        int start = text.indexOf("```");
        if (start == -1) return null;
        int nl = text.indexOf("\n", start);
        int cs = nl != -1 ? nl + 1 : start + 3;
        int end = text.indexOf("```", cs);
        if (end == -1) return null;
        return text.substring(cs, end).trim();
    }

    private static JTextComponent getActiveEditor() {
        java.awt.Component c = java.awt.KeyboardFocusManager
                .getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        return (c instanceof JTextComponent) ? (JTextComponent) c : null;
    }
}
