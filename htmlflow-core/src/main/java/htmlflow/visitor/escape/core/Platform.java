package htmlflow.visitor.escape.core;

/**
 * A utility class that provides a thread-local character buffer for escaping characters.
 * <p>
 * This class is used to manage a thread-local buffer that can be reused for character escaping,
 * reducing the need for frequent allocations and deallocations of character arrays.
 * <p>
 * The buffer is initialized with a default size, and if it grows beyond this size,
 * it will not be returned to the thread-local storage, allowing it to grow as needed.
 */
final class Platform {
    private Platform() {}

    /**
     * Returns a thread-local character buffer for escaping characters.
     * <p>
     * This method retrieves a character array from a thread-local storage.
     * The buffer is used to avoid frequent allocations and deallocations
     * of character arrays during the escaping process.
     * <p>
     * If the buffer is null, it throws an {@link IllegalStateException}.
     * This is a safeguard to ensure that the buffer is always available
     * when needed for character escaping.
     *
     * @return a character array from the thread-local buffer
     * @throws IllegalStateException if the thread-local buffer is null
     */
    static char[] charBufferFromTheadLocal() {
        char[] buffer = THREAD_LOCAL_BUFFER.get();
        if (buffer == null ) {
            throw new IllegalStateException("Thread local buffer is null");
        }
        return buffer;
    }

    /**
     * The size of the thread-local buffer used for character escaping.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * A thread-local buffer for character escaping.
     * <p>
     * This buffer is used to avoid frequent allocations and deallocations
     * of character arrays during the escaping process.
     * <p>
     * If it grows past the {@link #BUFFER_SIZE} it don't put it back into the thread local, it just keeps
     * going and grows as needed.
     */
    private static final ThreadLocal<char[]> THREAD_LOCAL_BUFFER = new ThreadLocal<char[]>() {
        @Override
        protected char[] initialValue() {
            return new char[BUFFER_SIZE];
        }
    };
}
