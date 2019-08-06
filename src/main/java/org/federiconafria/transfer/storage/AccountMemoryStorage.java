package org.federiconafria.transfer.storage;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;
import org.federiconafria.transfer.services.interfaces.AccountStorage;

import java.util.HashMap;
import java.util.Map;

public class AccountMemoryStorage implements AccountStorage {

    private Map<Long, Account> accounts = new HashMap<>();

    @Override
    public void insertAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    @Override
    public Account getAccount(long id) throws AccountNotFoundException {
        if (accounts.containsKey(id)) {
            return accounts.get(id);
        } else {
            throw new AccountNotFoundException(String.format("Account %s not found", id));
        }
    }
}
