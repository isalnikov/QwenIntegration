package org.netbeans.modules.qwen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for QwenDiffApplier.extractCodeBlock.
 */
class QwenDiffApplierTest {

    @Test
    void extractsCodeBlockWithLanguage() {
        String input = "Here is the code:\n```java\npublic class Hello {}\n```\nDone.";
        String result = org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(input);
        assertEquals("public class Hello {}", result);
    }

    @Test
    void extractsCodeBlockWithoutLanguage() {
        String input = "```\nSystem.out.println(\"hi\");\n```";
        String result = org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(input);
        assertEquals("System.out.println(\"hi\");", result);
    }

    @Test
    void returnsNullWhenNoCodeBlock() {
        String input = "Just plain text, no fences.";
        assertNull(org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(input));
    }

    @Test
    void returnsNullForEmptyInput() {
        assertNull(org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(""));
    }

    @Test
    void returnsNullForNullInput() {
        assertNull(org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(null));
    }

    @Test
    void handlesMultilineCodeBlock() {
        String input = "```python\ndef hello():\n    print('world')\n```";
        String result = org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(input);
        assertEquals("def hello():\n    print('world')", result);
    }

    @Test
    void handlesUnclosedFence() {
        String input = "```java\nint x = 1;";
        assertNull(org.netbeans.modules.qwen.editor.QwenDiffApplier.extractCodeBlock(input));
    }
}
