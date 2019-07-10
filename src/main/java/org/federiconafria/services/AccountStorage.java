package org.federiconafria.services;

import org.federiconafria.entities.Account;

interface AccountStorage {
    void insertAccount(Account account);

    Account getAccount(long id) throws AccountNotFoundException;

    public static class AccountNotFoundException extends Exception {
    }
}
