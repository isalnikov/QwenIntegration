package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Diagnostic action — shows plugin info dialog.
 * Confirms the plugin is installed and working.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.CheckAction")
@ActionRegistration(displayName = "#CTL_CheckAction", lazy = false)
@Messages("CTL_CheckAction=Qwen: Plugin Check ✓")
public final class CheckAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = "✅ Qwen Plugin is WORKING!\n\n"
                + "Plugin: Qwen Code Plugin v1.0.0\n"
                + "Status: Actions registered successfully\n"
                + "Menu: Qwen AI (in menu bar)\n"
                + "Toolbar: Qwen AI (View → Toolbars → Qwen AI)\n"
                + "Context menu: right-click in any text editor\n"
                + "\n"
                + "Actions:\n"
                + "  • Qwen: Explain Code\n"
                + "  • Qwen: Generate Code\n"
                + "  • Qwen: Refactor / Optimize\n"
                + "  • Qwen: Write Unit Tests\n"
                + "  • Qwen: Find Vulnerabilities\n"
                + "  • Qwen: Plugin Check ✓\n"
                + "  • Qwen: Open Console\n"
                + "\n"
                + "If you see this, all registrations work correctly!";

        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(
                msg, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
}
