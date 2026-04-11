package org.netbeans.modules.qwen.core;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks session state: tokens, messages, activity.
 */
public final class QwenSession {

    private final String id;
    private final AtomicInteger tokens = new AtomicInteger(0);
    private final AtomicInteger messages = new AtomicInteger(0);
    private volatile boolean active;

    public QwenSession() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
    }

    public String getId() { return id; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
    public int getTokens() { return tokens.get(); }
    public int getMessages() { return messages.get(); }

    public void recordUsage(String request, String response) {
        int r = Math.max(1, request.length() / 4);
        int s = Math.max(1, response.length() / 4);
        tokens.addAndGet(r + s);
        messages.incrementAndGet();
    }

    public void clear() {
        tokens.set(0);
        messages.set(0);
        active = false;
    }

    public String stats() {
        return "Session " + id + " — tokens: " + tokens.get() + ", messages: " + messages.get();
    }

    @Override
    public String toString() {
        return "QwenSession{id=" + id + ", active=" + active +
               ", tokens=" + tokens.get() + ", messages=" + messages.get() + "}";
    }
}
