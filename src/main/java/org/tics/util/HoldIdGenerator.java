package org.tics.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generator for reservation hold id.
 */
public class HoldIdGenerator {

    private AtomicInteger lastHoldId;

    public HoldIdGenerator() {
        this(0);
    }

    public HoldIdGenerator(int start) {
        lastHoldId = new AtomicInteger(start);
    }

    public int nextId() {
        return lastHoldId.incrementAndGet();
    }

}