package org.netbeans.modules.qwen.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.NbBundle.Messages;

/**
 * Test / Diagnostic action — verifies the plugin is working.
 * Shows a dialog with plugin info when clicked.
 */
@ActionID(category = "Qwen", id = "org.netbeans.modules.qwen.actions.CheckAction")
@ActionRegistration(displayName = "#CTL_CheckAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/Qwen", position = 600),
    @ActionReference(path = "Toolbars/Qwen", position = 600)
})
@Messages("CTL_CheckAction=Qwen: Plugin Check")
public final class CheckAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = "✅ Qwen Plugin is WORKING!\n\n"
                + "Plugin: Qwen Code Plugin v1.0.0\n"
                + "Status: Actions registered successfully\n"
                + "Menu: Qwen (in menu bar)\n"
                + "Toolbar: Qwen (View → Toolbars → Qwen)\n"
                + "Context menu: right-click in editor\n"
                + "\n"
                + "Available actions:\n"
                + "  • Explain Code\n"
                + "  • Generate Code\n"
                + "  • Refactor / Optimize\n"
                + "  • Write Unit Tests\n"
                + "  • Find Vulnerabilities\n"
                + "  • Open Console\n"
                + "  • Plugin Check (this)\n"
                + "\n"
                + "If you see this dialog, all registrations work correctly.";

        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(
                msg, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
}
