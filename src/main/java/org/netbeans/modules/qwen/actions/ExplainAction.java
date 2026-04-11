package org.netbeans.modules.qwen.actions;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Explain selected code using Qwen AI.
 * Registered via layer.xml: Menu, Toolbar, and Editor context menu.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.ExplainAction")
@ActionRegistration(displayName = "#CTL_ExplainAction", lazy = false)
@Messages("CTL_ExplainAction=Qwen: Explain Code")
public final class ExplainAction extends AbstractAction {

    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent ed = getEditor();
        if (ed == null) { warn("No editor active."); return; }
        String t = getText(ed);
        if (t.isEmpty()) { warn("No text."); return; }
        run(t);
    }

    private void run(String code) {
        try {
            IO.getOut().println("=== Qwen: Explain Code ===");
            IO.getOut().println("Selected " + code.length() + " characters");
            IO.getOut().println("Ready to send to qwen-code-cli");
            IO.select();
        } catch (Exception ex) {}
    }

    private String getText(JTextComponent ed) {
        String s = ed.getSelectedText();
        return (s == null || s.isEmpty()) ? ed.getText() : s;
    }

    private static JTextComponent getEditor() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        return (c instanceof JTextComponent) ? (JTextComponent) c : null;
    }

    private static void warn(String msg) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE));
    }
}
