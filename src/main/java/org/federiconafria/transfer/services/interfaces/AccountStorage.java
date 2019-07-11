package org.federiconafria.transfer.services.interfaces;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;

public interface AccountStorage {
    void insertAccount(Account account);

    Account getAccount(long id) throws AccountNotFoundException;

}
