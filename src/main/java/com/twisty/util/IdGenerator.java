package com.twisty.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private IdGenerator() { }

    /**
     * Generates the next unique ID.
     *
     * @return a monotonically increasing long ID
     */
    public static Long nextId() {
        return COUNTER.incrementAndGet();
    }

    /**
     * Resets the counter (useful for integration tests).
     */
    public static void reset() {
        COUNTER.set(0);
    }

    /**
     * Returns the current value without incrementing.
     *
     * @return current ID value
     */
    public static Long current() {
        return COUNTER.get();
    }
}
