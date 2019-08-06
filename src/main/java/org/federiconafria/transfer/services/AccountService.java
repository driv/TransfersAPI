package org.federiconafria.transfer.services;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;
import org.federiconafria.transfer.services.interfaces.AccountIdProvider;
import org.federiconafria.transfer.services.interfaces.AccountStorage;

public class AccountService {

    private final AccountStorage storage;
    private final AccountIdProvider idProvider;

    public AccountService(AccountStorage storage, AccountIdProvider idProvider) {
        this.storage = storage;
        this.idProvider = idProvider;
    }

    public Account createAccount(Account inputData) {
        if (inputData == null) throw new IllegalArgumentException("Account should not be null");

        long id = idProvider.generateNextId();
        storage.insertAccount(new Account(id, inputData));
        try {
            return storage.getAccount(id);
        } catch (AccountNotFoundException e) {
            throw new IllegalStateException("The created account cannot be found");
        }
    }

    public Account getAccount(long id) throws AccountNotFoundException {
        return storage.getAccount(id);
    }
}
