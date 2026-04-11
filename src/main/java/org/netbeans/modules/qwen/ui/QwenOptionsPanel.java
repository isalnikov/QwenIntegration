package org.netbeans.modules.qwen.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import org.netbeans.modules.qwen.core.Bundle;
import org.netbeans.modules.qwen.core.QwenCliDetector;
import org.netbeans.modules.qwen.core.QwenPreferences;

/**
 * Swing panel for Tools > Options > Qwen AI page.
 */
final class QwenOptionsPanel extends JPanel {

    private final QwenPreferences prefs;
    private boolean changed;
    private JTextField tfCli, tfModel, tfTimeout;
    private JLabel lblStatus;

    QwenOptionsPanel(QwenPreferences prefs) {
        this.prefs = prefs;
        init();
        load();
    }

    private void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // CLI Path
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        add(new JLabel(Bundle.opts_cli_path()), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0;
        tfCli = new JTextField(30);
        add(tfCli, c);
        onChange(tfCli);
        c.gridx = 2; c.gridy = 0; c.weightx = 0;
        JButton btnBrowse = new JButton("...");
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfCli.setText(fc.getSelectedFile().getAbsolutePath());
                changed = true;
                checkStatus();
            }
        });
        add(btnBrowse, c);

        // Model
        c.gridx = 0; c.gridy = 1; c.weightx = 0; c.gridwidth = 1;
        add(new JLabel(Bundle.opts_model()), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1.0; c.gridwidth = 2;
        tfModel = new JTextField();
        add(tfModel, c);
        onChange(tfModel);
        c.gridwidth = 1;

        // Timeout
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        add(new JLabel(Bundle.opts_timeout()), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1.0; c.gridwidth = 2;
        tfTimeout = new JTextField();
        add(tfTimeout, c);
        onChange(tfTimeout);
        c.gridwidth = 1;

        // Status
        c.gridx = 0; c.gridy = 3; c.gridwidth = 3;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.SOUTHWEST;
        lblStatus = new JLabel(" ");
        add(lblStatus, c);

        checkStatus();
    }

    private void onChange(JTextField tf) {
        tf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { changed = true; }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { changed = true; }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { changed = true; }
        });
    }

    void load() {
        tfCli.setText(prefs.getCliPath());
        tfModel.setText(prefs.getModel());
        tfTimeout.setText(String.valueOf(prefs.getTimeout()));
        changed = false;
        checkStatus();
    }

    void save() {
        prefs.setCliPath(tfCli.getText().trim());
        try { prefs.setModel(tfModel.getText().trim()); } catch (Exception e) { prefs.setModel(QwenPreferences.DEFAULT_MODEL); }
        try { prefs.setTimeout(Integer.parseInt(tfTimeout.getText().trim())); } catch (Exception e) { prefs.setTimeout(QwenPreferences.DEFAULT_TIMEOUT); }
        changed = false;
        checkStatus();
    }

    boolean isChanged() { return changed; }

    private void checkStatus() {
        String path = tfCli.getText().trim();
        if (QwenCliDetector.isQwenAvailable(path)) {
            lblStatus.setText("✔ Qwen Code CLI detected");
            lblStatus.setForeground(new java.awt.Color(0, 128, 0));
        } else {
            lblStatus.setText("✘ Qwen Code CLI not found");
            lblStatus.setForeground(java.awt.Color.RED);
        }
    }
}
