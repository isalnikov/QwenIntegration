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
 * Refactor / Optimize code using Qwen AI.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.RefactorAction")
@ActionRegistration(displayName = "#CTL_RefactorAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Editors/text/plain/Popup", position = 452),
    @ActionReference(path = "Menu/Qwen", position = 300),
    @ActionReference(path = "Toolbars/Qwen", position = 300)
})
@Messages("CTL_RefactorAction=Qwen: Refactor / Optimize")
public final class RefactorAction extends AbstractAction {
    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);
    @Override public void actionPerformed(ActionEvent e) { run("Refactor / Optimize"); }
    private void run(String a) { JTextComponent ed = get(); if(ed==null){warn("No editor.");return;} out(a+": "+len(ed)); }
    private String len(JTextComponent e){String s=e.getSelectedText();return(s==null||s.isEmpty())?String.valueOf(e.getText().length()):String.valueOf(s.length());}
    private void out(String l){try{IO.getOut().println(l);IO.select();}catch(Exception ex){}}
    private JTextComponent get(){Component c=KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();return(c instanceof JTextComponent)?(JTextComponent)c:null;}
    private void warn(String m){DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(m,NotifyDescriptor.WARNING_MESSAGE));}
}
