package htmlflow.visitor.escape.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlatformTest {
    @Test
    void charBufferFromTheadLocal_should_return_non_null_buffer() {
        char[] buffer = Platform.charBufferFromTheadLocal();
        assertNotNull(buffer, "Thread local buffer should not be null");
    }
}