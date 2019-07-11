package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.services.AccountIdProvider;

public class MyAccountIdProvider implements AccountIdProvider {
    private static long nextId = 0;

    @Override
    public long generateNextId() {
        return ++nextId;
    }
}
