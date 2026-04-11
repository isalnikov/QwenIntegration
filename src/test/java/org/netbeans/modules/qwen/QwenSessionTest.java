package org.netbeans.modules.qwen;

import org.junit.jupiter.api.Test;
import org.netbeans.modules.qwen.core.QwenSession;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for QwenSession.
 */
class QwenSessionTest {

    @Test
    void newSessionIsInactive() {
        QwenSession s = new QwenSession();
        assertFalse(s.isActive());
    }

    @Test
    void setActiveWorks() {
        QwenSession s = new QwenSession();
        s.setActive(true);
        assertTrue(s.isActive());
    }

    @Test
    void recordsTokenUsage() {
        QwenSession s = new QwenSession();
        s.recordUsage("hello world", "foo bar baz");
        assertTrue(s.getMessages() > 0);
        assertTrue(s.getTokens() > 0);
    }

    @Test
    void clearResetsAll() {
        QwenSession s = new QwenSession();
        s.setActive(true);
        s.recordUsage("request", "response");
        s.clear();
        assertFalse(s.isActive());
        assertEquals(0, s.getTokens());
        assertEquals(0, s.getMessages());
    }

    @Test
    void idIsNotNull() {
        QwenSession s = new QwenSession();
        assertNotNull(s.getId());
        assertEquals(8, s.getId().length());
    }

    @Test
    void statsReturnsString() {
        QwenSession s = new QwenSession();
        s.recordUsage("test", "result");
        String stats = s.stats();
        assertNotNull(stats);
        assertTrue(stats.contains("tokens"));
        assertTrue(stats.contains("messages"));
    }

    @Test
    void multipleRecordsAccumulate() {
        QwenSession s = new QwenSession();
        s.recordUsage("req1", "resp1");
        s.recordUsage("req2", "resp2");
        assertEquals(2, s.getMessages());
    }
}
