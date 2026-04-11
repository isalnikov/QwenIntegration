package org.netbeans.modules.qwen.ui;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.qwen.core.QwenPreferences;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Options panel controller: Tools → Options → Qwen AI
 */
@OptionsPanelController.SubRegistration(displayName = "#CTL_Options")
@Messages("CTL_Options=Qwen AI")
public final class QwenOptionsController extends OptionsPanelController {

    private QwenOptionsPanel panel;
    private final QwenPreferences prefs;

    public QwenOptionsController() {
        this.prefs = new QwenPreferences();
    }

    @Override public void update() { getPanel().load(); }
    @Override public void applyChanges() { getPanel().save(); }
    @Override public void cancel() {}
    @Override public boolean isValid() { return true; }
    @Override public boolean isChanged() { return getPanel().isChanged(); }
    @Override public HelpCtx getHelpCtx() { return HelpCtx.DEFAULT_HELP; }
    @Override public void addPropertyChangeListener(PropertyChangeListener l) {}
    @Override public void removePropertyChangeListener(PropertyChangeListener l) {}
    @Override public JComponent getComponent(Lookup masterLookup) { return getPanel(); }

    private QwenOptionsPanel getPanel() {
        if (panel == null) panel = new QwenOptionsPanel(prefs);
        return panel;
    }
}
