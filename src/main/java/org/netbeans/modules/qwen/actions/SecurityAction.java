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
 * Find vulnerabilities using Qwen AI.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.SecurityAction")
@ActionRegistration(displayName = "#CTL_SecurityAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Editors/text/plain/Popup", position = 454),
    @ActionReference(path = "Menu/Qwen", position = 500),
    @ActionReference(path = "Toolbars/Qwen", position = 500)
})
@Messages("CTL_SecurityAction=Qwen: Find Vulnerabilities")
public final class SecurityAction extends AbstractAction {
    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", true);
    @Override public void actionPerformed(ActionEvent e) { JTextComponent ed = get(); if(ed==null){warn("No editor.");return;} try{IO.getOut().println("[Security Scan] selected");IO.select();}catch(Exception ex){}}
    private JTextComponent get(){Component c=KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();return(c instanceof JTextComponent)?(JTextComponent)c:null;}
    private void warn(String m){DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(m,NotifyDescriptor.WARNING_MESSAGE));}
}
