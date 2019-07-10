package org.federiconafria.transfer.services;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;

import java.math.BigDecimal;

public class AccountService {

    private final AccountStorage storage;
    private final AccountIdProvider idProvider;

    public AccountService(AccountStorage storage, AccountIdProvider idProvider) {
        this.storage = storage;
        this.idProvider = idProvider;
    }

    public void createAccount(Account inputData) {
        if (inputData == null) throw new IllegalArgumentException("Account should not be null");
        if (inputData.getUser() == null) throw new IllegalArgumentException("Invalid user");
        if (inputData.getAmount() == null || inputData.getAmount().getAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount should be a positive number");

        storage.insertAccount(new Account(idProvider.generateNextId(), inputData));
    }

    public Account getAccount(long id) throws AccountNotFoundException {
        try {
            return storage.getAccount(id);
        } catch (AccountNotFoundException e) {
            throw e;
        }
    }
}
