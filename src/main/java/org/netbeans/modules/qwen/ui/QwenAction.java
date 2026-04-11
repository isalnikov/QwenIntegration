package org.netbeans.modules.qwen.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.qwen.core.Bundle;
import org.netbeans.modules.qwen.core.QwenCliClient;
import org.netbeans.modules.qwen.core.QwenLogger;
import org.netbeans.modules.qwen.core.QwenPreferences;
import org.netbeans.modules.qwen.core.QwenSession;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * All Action classes for menu items, toolbar buttons, and context menu.
 * Each action: gets selected text → sends to CLI → writes to output window.
 */
public abstract class QwenAction extends AbstractAction {

    private static final InputOutput IO = IOProvider.getDefault().getIO("Qwen AI", false);
    private static final QwenPreferences prefs = new QwenPreferences();

    protected QwenAction(String name) {
        super(name);
    }

    protected abstract String getPrompt();

    @Override
    public void actionPerformed(ActionEvent e) {
        JTextComponent editor = getActiveEditor();
        if (editor == null) {
            showWarn(Bundle.msg_no_editor());
            return;
        }
        String text = editor.getSelectedText();
        if (text == null || text.isEmpty()) {
            text = editor.getText();
        }
        if (text == null || text.isEmpty()) {
            showWarn(Bundle.msg_no_selection());
            return;
        }
        execute(getValue(Action.NAME).toString(), text);
    }

    private void execute(String name, String code) {
        QwenLogger.getInstance().info("Action: " + name);
        QwenSession session = new QwenSession();
        session.setActive(true);

        showOutput("[Running] " + name);
        QwenCliClient client = new QwenCliClient(prefs);
        client.executeAsync(getPrompt(), code, new QwenCliClient.Callback() {
            @Override public void onOutput(String line) {
                showOutput(line);
            }
            @Override public void onError(String error) {
                showOutput("[ERROR] " + error);
                QwenLogger.getInstance().warning("CLI error: " + error);
            }
            @Override public void onComplete() {
                showOutput("\n[Done]");
                session.recordUsage(getPrompt(), getCurrentOutput());
                QwenLogger.getInstance().info("Completed: " + name + " — " + session.stats());
            }
        });
    }

    private void showOutput(String line) {
        try {
            OutputWriter out = IO.getOut();
            out.println(line);
            IO.select();
        } catch (Exception ex) {
            // ignore
        }
    }

    private String getCurrentOutput() {
        // Approximate — we track it in the session
        return "";
    }

    private static void showWarn(String msg) {
        NotifyDescriptor.Message md = new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE);
        DialogDisplayer.getDefault().notify(md);
    }

    private static JTextComponent getActiveEditor() {
        java.awt.Component c = java.awt.KeyboardFocusManager
                .getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        return (c instanceof JTextComponent) ? (JTextComponent) c : null;
    }

    // ===== Concrete Actions =====

    public static class Explain extends QwenAction {
        public Explain() { super(Bundle.action_Explain()); }
        @Override protected String getPrompt() { return "Explain this code clearly."; }
    }

    public static class Generate extends QwenAction {
        public Generate() { super(Bundle.action_Generate()); }
        @Override protected String getPrompt() { return "Generate code for this description."; }
    }

    public static class Refactor extends QwenAction {
        public Refactor() { super(Bundle.action_Refactor()); }
        @Override protected String getPrompt() { return "Refactor and optimize this code. Explain changes."; }
    }

    public static class Test extends QwenAction {
        public Test() { super(Bundle.action_Test()); }
        @Override protected String getPrompt() { return "Write comprehensive unit tests for this code."; }
    }

    public static class Security extends QwenAction {
        public Security() { super(Bundle.action_Security()); }
        @Override protected String getPrompt() { return "Find security vulnerabilities and bugs in this code."; }
    }

    public static class Agent extends AbstractAction {
        public Agent() { super(Bundle.action_Agent()); }
        @Override
        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine dlg = new NotifyDescriptor.InputLine("Describe what to do:", "Qwen Agent");
            if (DialogDisplayer.getDefault().notify(dlg) == NotifyDescriptor.OK_OPTION) {
                String input = dlg.getInputText();
                if (input != null && !input.isEmpty()) {
                    QwenLogger.getInstance().info("Agent: " + input);
                    showOutput("Agent: " + input);
                    QwenSession session = new QwenSession();
                    session.setActive(true);
                    QwenCliClient client = new QwenCliClient(new QwenPreferences());
                    client.executeAsync(input, null, new QwenCliClient.Callback() {
                        @Override public void onOutput(String line) { showOutput(line); }
                        @Override public void onError(String error) { showOutput("[ERROR] " + error); }
                        @Override public void onComplete() {
                            showOutput("\n[Agent Done]");
                            QwenLogger.getInstance().info("Agent completed");
                        }
                    });
                }
            }
        }
        private void showOutput(String line) {
            try {
                OutputWriter out = IO.getOut();
                out.println(line);
                IO.select();
            } catch (Exception ex) {}
        }
    }

    public static class OpenConsole extends AbstractAction {
        public OpenConsole() { super(Bundle.action_Console()); }
        @Override
        public void actionPerformed(ActionEvent e) {
            QwenConsoleTopComponent tc = QwenConsoleTopComponent.findInstance();
            tc.open();
            tc.requestActive();
        }
    }
}
