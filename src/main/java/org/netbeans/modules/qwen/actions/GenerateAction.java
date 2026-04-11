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
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Generate code using Qwen AI.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.GenerateAction")
@ActionRegistration(displayName = "#CTL_GenerateAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Editors/text/plain/Popup", position = 451),
    @ActionReference(path = "Menu/Qwen", position = 200),
    @ActionReference(path = "Toolbars/Qwen", position = 200)
})
@Messages("CTL_GenerateAction=Qwen: Generate Code")
public final class GenerateAction extends AbstractAction {
    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);
    @Override public void actionPerformed(ActionEvent e) {
        JTextComponent ed = getEditor();
        if (ed == null) { warn("No editor active."); return; }
        String t = getText(ed);
        if (t.isEmpty()) { warn("No text."); return; }
        out("Generate: " + t);
    }
    private String getText(JTextComponent ed) { String s = ed.getSelectedText(); return (s==null||s.isEmpty())? ed.getText() : s; }
    private void out(String l) { try { IO.getOut().println(l); IO.select(); } catch(Exception ex){} }
    private static JTextComponent getEditor() { Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner(); return (c instanceof JTextComponent)?(JTextComponent)c:null; }
    private static void warn(String m) { DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(m, NotifyDescriptor.WARNING_MESSAGE)); }
}
