package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.qwen.ui.QwenConsoleTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Opens the Qwen AI Console window.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.OpenConsoleAction")
@ActionRegistration(displayName = "#CTL_OpenConsoleAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Qwen", position = 700),
    @ActionReference(path = "Toolbars/Qwen", position = 700),
    @ActionReference(path = "Menu/Window", position = 1000)
})
@Messages("CTL_OpenConsoleAction=Qwen: Open Console")
public final class OpenConsoleAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("QwenConsoleTopComponent");
        if (tc == null) {
            tc = new QwenConsoleTopComponent();
        }
        if (!tc.isOpened()) {
            tc.open();
        }
        tc.requestActive();
    }
}
