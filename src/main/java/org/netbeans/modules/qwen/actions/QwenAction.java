package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Base class for all Qwen editor actions.
 * Registered via annotations for menu, toolbar, and context menu.
 */
@ActionID(
    category = "Qwen",
    id = "org.netbeans.modules.qwen.actions.QwenAction"
)
@ActionRegistration(
    displayName = "#CTL_QwenAction",
    lazy = false
)
@ActionReferences({
    @ActionReference(path = "Editors/text/plain/Popup", position = 1000),
    @ActionReference(path = "Menu/Qwen", position = 1000),
    @ActionReference(path = "Toolbars/Qwen", position = 1000)
})
@Messages("CTL_QwenAction=Qwen Action")
public class QwenAction extends AbstractAction {

    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);

    protected final String prompt;

    public QwenAction() {
        this("Explain this code.");
    }

    public QwenAction(String prompt) {
        super(prompt);
        this.prompt = prompt;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent editor = getActiveEditor();
        if (editor == null) {
            showWarn("No editor is active. Open a file first.");
            return;
        }
        String text = editor.getSelectedText();
        if (text == null || text.isEmpty()) {
            text = editor.getText();
        }
        if (text == null || text.isEmpty()) {
            showWarn("No text selected.");
            return;
        }
        execute(prompt, text);
    }

    protected void execute(String prompt, String code) {
        showOutput("[Running] " + prompt);
        IO.select();
    }

    protected void showOutput(String line) {
        try {
            IO.getOut().println(line);
        } catch (Exception ex) { /* ignore */ }
    }

    protected static JTextComponent getActiveEditor() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        return (c instanceof JTextComponent) ? (JTextComponent) c : null;
    }

    protected static void showWarn(String msg) {
        NotifyDescriptor.Message md = new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(md);
    }
}
