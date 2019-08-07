package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountMemoryStorage implements AccountStorage {

    private Map<Long, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void insertAccount(Account account) {
        accounts.put(account.getId(), new Account(account));
    }

    @Override
    public Account getAccount(long id) throws EntityNotFoundException {
        if (accounts.containsKey(id)) {
            return new Account(accounts.get(id));
        } else {
            throw new EntityNotFoundException(String.format("Account %s not found", id));
        }
    }
}
