package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.logic.api.TransferStorage;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransferMemoryStorage implements TransferStorage {
    private final Map<Long, Transfer> transfers = new ConcurrentHashMap<>();

    @Override
    public void insertTransfer(Transfer transfer) {
        transfers.put(transfer.getId(), new Transfer(transfer));
    }

    @Override
    public Transfer getTransfer(long id) throws EntityNotFoundException {
        if (transfers.containsKey(id)) {
            return new Transfer(transfers.get(id));
        } else {
            throw new EntityNotFoundException(String.format("Account %s not found", id));
        }
    }
}
