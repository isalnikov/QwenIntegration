package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.qwen.ui.QwenConsoleTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Opens the Qwen AI Console window.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.OpenConsoleAction")
@ActionRegistration(displayName = "#CTL_OpenConsoleAction", lazy = false)
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
