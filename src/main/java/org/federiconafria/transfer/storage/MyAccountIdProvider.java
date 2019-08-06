package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.services.interfaces.AccountIdProvider;

public class MyAccountIdProvider implements AccountIdProvider {
    private long nextId = 0;

    @Override
    public synchronized long generateNextId() {
        return ++nextId;
    }
}
