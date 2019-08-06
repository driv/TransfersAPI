package org.federiconafria.transfer.logic.services;

import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.api.TransferStorage;
import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TransferExecutionService {
    private final TransferStorage transferStorage;
    private final AccountStorage accountStorage;
    private final ConcurrentMap<Integer, Integer> accountsLocks;

    public TransferExecutionService(TransferStorage transferStorage, AccountStorage accountStorage) {
        this.transferStorage = transferStorage;
        this.accountStorage = accountStorage;
        this.accountsLocks = new ConcurrentHashMap<>();
    }

    public synchronized void execute(long l) throws EntityNotFoundException {
        Transfer transfer = transferStorage.getTransfer(l);
        Account source = accountStorage.getAccount(transfer.getIdSourceAccount());
        Account destination = accountStorage.getAccount(transfer.getIdDestinationAccount());

        source.withdraw(transfer.getAmount());
        if (source.getBalance().getAmount().compareTo(BigDecimal.ZERO) >= 0) {
            destination.deposit(transfer.getAmount());
            transfer.execute();
            accountStorage.insertAccount(source);
            accountStorage.insertAccount(destination);
        } else {
            transfer.fail();
        }

        transferStorage.insertTransfer(transfer);
    }
}
