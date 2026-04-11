package org.netbeans.modules.qwen.core;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Centralized logger that bridges java.util.logging to the QwenConsole.
 * All plugin components should log through this.
 */
public final class QwenLogger {

    public static final String LOG_NAME = "org.netbeans.modules.qwen";
    private static final Logger LOGGER = Logger.getLogger(LOG_NAME);

    private static volatile QwenLogger instance;

    private ConsoleSink sink;

    public static synchronized QwenLogger getInstance() {
        if (instance == null) {
            instance = new QwenLogger();
        }
        return instance;
    }

    private QwenLogger() {
        // Set up the root logger for the qwen package
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(new Handler() {
            @Override public void publish(LogRecord record) {
                if (sink != null) {
                    sink.append(format(record));
                }
            }
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        });
    }

    /**
     * Attach a console sink to receive log messages.
     */
    public void setSink(ConsoleSink s) {
        this.sink = s;
    }

    public void log(Level level, String msg) {
        LOGGER.log(level, msg);
    }

    public void log(Level level, String msg, Throwable t) {
        LOGGER.log(level, msg, t);
    }

    public void info(String msg) { log(Level.INFO, msg); }
    public void warning(String msg) { log(Level.WARNING, msg); }
    public void severe(String msg) { log(Level.SEVERE, msg); }
    public void fine(String msg) { log(Level.FINE, msg); }

    private String format(LogRecord record) {
        String level = "[" + record.getLevel().getName() + "] ";
        String msg = record.getMessage();
        if (record.getThrown() != null) {
            msg += " — " + record.getThrown().getMessage();
        }
        return level + msg;
    }

    /**
     * Interface for receiving log output.
     */
    public interface ConsoleSink {
        void append(String line);
    }
}
