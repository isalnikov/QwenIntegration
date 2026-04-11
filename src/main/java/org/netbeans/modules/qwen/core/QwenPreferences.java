package org.netbeans.modules.qwen.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;

/**
 * Manages plugin preferences. Persisted between IDE sessions.
 */
public final class QwenPreferences {

    private static final String NODE = "org/netbeans/modules/qwen";
    private static final String K_CLI = "cliPath";
    private static final String K_MODEL = "model";
    private static final String K_TIMEOUT = "timeout";

    public static final String DEFAULT_CLI = "";
    public static final String DEFAULT_MODEL = "qwen-coder";
    public static final int DEFAULT_TIMEOUT = 120;

    private final Preferences prefs;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public QwenPreferences() {
        prefs = NbPreferences.forModule(QwenPreferences.class).node(NODE);
    }

    public String getCliPath() {
        return prefs.get(K_CLI, DEFAULT_CLI);
    }

    public void setCliPath(String v) {
        String old = getCliPath();
        prefs.put(K_CLI, v == null ? DEFAULT_CLI : v);
        support.firePropertyChange(K_CLI, old, getCliPath());
    }

    public String getModel() {
        return prefs.get(K_MODEL, DEFAULT_MODEL);
    }

    public void setModel(String v) {
        String old = getModel();
        prefs.put(K_MODEL, v == null ? DEFAULT_MODEL : v);
        support.firePropertyChange(K_MODEL, old, getModel());
    }

    public int getTimeout() {
        return prefs.getInt(K_TIMEOUT, DEFAULT_TIMEOUT);
    }

    public void setTimeout(int v) {
        int old = getTimeout();
        prefs.putInt(K_TIMEOUT, Math.max(10, v));
        support.firePropertyChange(K_TIMEOUT, old, getTimeout());
    }

    public String getEffectiveCliPath() {
        return QwenCliDetector.resolvePath(getCliPath());
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(WeakListeners.propertyChange(l, support));
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }
}
