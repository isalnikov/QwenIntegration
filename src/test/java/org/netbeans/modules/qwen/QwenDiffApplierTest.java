package org.netbeans.modules.qwen;

import org.junit.jupiter.api.Test;
import org.netbeans.modules.qwen.editor.QwenDiffApplier;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for QwenDiffApplier.extractCodeBlock.
 */
class QwenDiffApplierTest {

    @Test
    void extractsCodeBlockWithLanguage() {
        String input = "Here is the code:\n```java\npublic class Hello {}\n```\nDone.";
        assertEquals("public class Hello {}", QwenDiffApplier.extractCodeBlock(input));
    }

    @Test
    void extractsCodeBlockWithoutLanguage() {
        String input = "```\nSystem.out.println(\"hi\");\n```";
        assertEquals("System.out.println(\"hi\");", QwenDiffApplier.extractCodeBlock(input));
    }

    @Test
    void returnsNullWhenNoCodeBlock() {
        assertNull(QwenDiffApplier.extractCodeBlock("Just plain text, no fences."));
    }

    @Test
    void returnsNullForEmptyInput() {
        assertNull(QwenDiffApplier.extractCodeBlock(""));
    }

    @Test
    void returnsNullForNullInput() {
        assertNull(QwenDiffApplier.extractCodeBlock(null));
    }

    @Test
    void handlesMultilineCodeBlock() {
        String input = "```python\ndef hello():\n    print('world')\n```";
        assertEquals("def hello():\n    print('world')", QwenDiffApplier.extractCodeBlock(input));
    }

    @Test
    void handlesUnclosedFence() {
        assertNull(QwenDiffApplier.extractCodeBlock("```java\nint x = 1;"));
    }

    @Test
    void handlesMultipleCodeBlocks_returnsFirst() {
        String input = "```java\nint a = 1;\n```\n\n```python\nprint('b')\n```";
        assertEquals("int a = 1;", QwenDiffApplier.extractCodeBlock(input));
    }
}
