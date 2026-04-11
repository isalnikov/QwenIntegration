package org.netbeans.modules.qwen.core;

import org.openide.util.NbBundle;

/**
 * Bundle wrapper for internationalization.
 */
public class Bundle {

    private Bundle() {}

    // ====== Actions ======

    public static String action_Explain() {
        return NbBundle.getMessage(Bundle.class, "action.explain");
    }
    public static String action_Generate() {
        return NbBundle.getMessage(Bundle.class, "action.generate");
    }
    public static String action_Refactor() {
        return NbBundle.getMessage(Bundle.class, "action.refactor");
    }
    public static String action_Test() {
        return NbBundle.getMessage(Bundle.class, "action.test");
    }
    public static String action_Security() {
        return NbBundle.getMessage(Bundle.class, "action.security");
    }
    public static String action_Agent() {
        return NbBundle.getMessage(Bundle.class, "action.agent");
    }
    public static String action_Console() {
        return NbBundle.getMessage(Bundle.class, "action.console");
    }

    // ====== UI ======

    public static String console_title() {
        return NbBundle.getMessage(Bundle.class, "console.title");
    }
    public static String console_placeholder() {
        return NbBundle.getMessage(Bundle.class, "console.placeholder");
    }
    public static String console_send() {
        return NbBundle.getMessage(Bundle.class, "console.send");
    }
    public static String console_clear() {
        return NbBundle.getMessage(Bundle.class, "console.clear");
    }

    // ====== Messages ======

    public static String msg_no_selection() {
        return NbBundle.getMessage(Bundle.class, "msg.no_selection");
    }
    public static String msg_no_editor() {
        return NbBundle.getMessage(Bundle.class, "msg.no_editor");
    }
    public static String msg_processing() {
        return NbBundle.getMessage(Bundle.class, "msg.processing");
    }
    public static String msg_cli_not_found() {
        return NbBundle.getMessage(Bundle.class, "msg.cli_not_found");
    }
    public static String msg_node_not_found() {
        return NbBundle.getMessage(Bundle.class, "msg.node_not_found");
    }
    public static String msg_welcome() {
        return NbBundle.getMessage(Bundle.class, "msg.welcome");
    }

    // ====== Options ======

    public static String opts_category() {
        return NbBundle.getMessage(Bundle.class, "opts.category");
    }
    public static String opts_cli_path() {
        return NbBundle.getMessage(Bundle.class, "opts.cli_path");
    }
    public static String opts_model() {
        return NbBundle.getMessage(Bundle.class, "opts.model");
    }
    public static String opts_timeout() {
        return NbBundle.getMessage(Bundle.class, "opts.timeout");
    }

    // ====== Menu / Toolbar display names ======

    public static String menu_name() {
        return NbBundle.getMessage(Bundle.class, "menu.name");
    }
    public static String toolbar_name() {
        return NbBundle.getMessage(Bundle.class, "toolbar.name");
    }
}
