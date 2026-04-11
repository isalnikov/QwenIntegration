package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.netbeans.modules.qwen.ui.QwenConsoleTopComponent;
import org.openide.awt.ActionID;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.OpenConsoleAction")
public final class OpenConsoleAction extends AbstractAction {
    public OpenConsoleAction() { putValue(NAME, "Qwen: Open Console"); }
    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("QwenConsoleTopComponent");
        if (tc == null) tc = new QwenConsoleTopComponent();
        if (!tc.isOpened()) tc.open();
        tc.requestActive();
    }
}
