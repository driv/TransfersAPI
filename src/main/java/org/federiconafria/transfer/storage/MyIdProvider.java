package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.logic.api.IdProvider;

import java.util.concurrent.atomic.AtomicLong;

public class MyIdProvider implements IdProvider {
    private final AtomicLong nextId = new AtomicLong(0);

    @Override
    public synchronized long generateNextId() {
        return nextId.incrementAndGet();
    }
}
