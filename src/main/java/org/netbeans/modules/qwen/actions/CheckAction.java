package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;

@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.CheckAction")
public final class CheckAction extends AbstractAction {
    public CheckAction() { putValue(NAME, "Qwen: Plugin Check"); }
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = "Qwen Plugin is WORKING!\n\n"
                + "Plugin: Qwen Code Plugin v1.0.0\n"
                + "Status: Actions registered via layer.xml\n"
                + "Menu: Qwen AI (in menu bar)\n"
                + "Toolbar: Qwen AI (View > Toolbars > Qwen)\n"
                + "Context menu: right-click in Java editor\n"
                + "\n"
                + "Actions:\n"
                + "  - Qwen: Explain Code\n"
                + "  - Qwen: Generate Code\n"
                + "  - Qwen: Refactor / Optimize\n"
                + "  - Qwen: Write Unit Tests\n"
                + "  - Qwen: Find Vulnerabilities\n"
                + "  - Qwen: Plugin Check\n"
                + "  - Qwen: Open Console\n"
                + "\n"
                + "If you see this, all registrations work correctly!";
        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
}
