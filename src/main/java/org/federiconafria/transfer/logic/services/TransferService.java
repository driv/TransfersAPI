package org.federiconafria.transfer.logic.services;

import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.api.IdProvider;
import org.federiconafria.transfer.logic.api.TransferQueue;
import org.federiconafria.transfer.logic.api.TransferStorage;

public class TransferService {

    private final TransferStorage transferStorage;
    private final TransferQueue transferQueue;
    private final AccountStorage accountStorage;
    private IdProvider transferIdProvider;

    public TransferService(TransferStorage transferStorage, TransferQueue transferQueue, AccountStorage accountStorage, IdProvider transferIdProvider) {
        this.transferStorage = transferStorage;
        this.transferQueue = transferQueue;
        this.accountStorage = accountStorage;
        this.transferIdProvider = transferIdProvider;
    }

    public Transfer createTransfer(Transfer input) throws EntityCreationException {
        validateInput(input);

        Transfer newTransfer = new Transfer(transferIdProvider.generateNextId(), input);
        transferStorage.insertTransfer(newTransfer);
        transferQueue.queueTransfer(newTransfer);
        return newTransfer;
    }

    private void validateInput(Transfer input) throws EntityCreationException {
        if (input == null) throw new EntityCreationException("No transfer provided");
        try {
            accountStorage.getAccount(input.getIdSourceAccount());
            accountStorage.getAccount(input.getIdDestinationAccount());
        } catch (EntityNotFoundException e) {
            throw new EntityCreationException("Invalid account", e);
        }
    }

    public Transfer getTransfer(long id) throws EntityNotFoundException {
        return transferStorage.getTransfer(id);
    }
}
