package org.federiconafria.transfer.logic.services;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.api.IdProvider;
import org.federiconafria.transfer.logic.api.AccountStorage;

public class AccountService {

    private final AccountStorage storage;
    private final IdProvider idProvider;

    public AccountService(AccountStorage storage, IdProvider idProvider) {
        this.storage = storage;
        this.idProvider = idProvider;
    }

    public Account createAccount(Account inputData) throws EntityCreationException {
        if (inputData == null) throw new EntityCreationException("Account should not be null");

        long id = idProvider.generateNextId();
        storage.insertAccount(new Account(id, inputData));
        try {
            return storage.getAccount(id);
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("The created account cannot be found");
        }
    }

    public Account getAccount(long id) throws EntityNotFoundException {
        return storage.getAccount(id);
    }
}
