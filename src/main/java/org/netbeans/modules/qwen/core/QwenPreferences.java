package org.netbeans.modules.qwen.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;

/** Plugin preferences, persisted between IDE sessions. */
public final class QwenPreferences {
    private static final String NODE = "org/netbeans/modules/qwen";
    private final Preferences prefs;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public static final String DEFAULT_MODEL = "qwen-coder";
    public static final int DEFAULT_TIMEOUT = 120;

    public QwenPreferences() { prefs = NbPreferences.forModule(QwenPreferences.class).node(NODE); }

    public String getCliPath() { return prefs.get("cliPath", ""); }
    public void setCliPath(String v) { String o = getCliPath(); prefs.put("cliPath", v == null ? "" : v); support.firePropertyChange("cliPath", o, getCliPath()); }

    public String getModel() { return prefs.get("model", DEFAULT_MODEL); }
    public void setModel(String v) { String o = getModel(); prefs.put("model", v == null ? DEFAULT_MODEL : v); support.firePropertyChange("model", o, getModel()); }

    public int getTimeout() { return prefs.getInt("timeout", DEFAULT_TIMEOUT); }
    public void setTimeout(int v) { int o = getTimeout(); prefs.putInt("timeout", Math.max(10, v)); support.firePropertyChange("timeout", o, getTimeout()); }

    public String getEffectiveCliPath() { return QwenCliDetector.resolvePath(getCliPath()); }

    public void addPropertyChangeListener(PropertyChangeListener l) { support.addPropertyChangeListener(WeakListeners.propertyChange(l, support)); }
    public void removePropertyChangeListener(PropertyChangeListener l) { support.removePropertyChangeListener(l); }
}
