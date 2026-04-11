package org.netbeans.modules.qwen.actions;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.RefactorAction")
public final class RefactorAction extends AbstractAction {
    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);
    public RefactorAction() { putValue(NAME, "Qwen: Refactor / Optimize"); }
    @Override public void actionPerformed(ActionEvent e) { JTextComponent ed = get(); if(ed==null){warn("No editor.");return;} try{IO.getOut().println("=== Qwen: Refactor ===");IO.select();}catch(Exception ex){}}
    private JTextComponent get(){Component c=KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();return(c instanceof JTextComponent)?(JTextComponent)c:null;}
    private void warn(String m){DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(m,NotifyDescriptor.WARNING_MESSAGE));}
}
