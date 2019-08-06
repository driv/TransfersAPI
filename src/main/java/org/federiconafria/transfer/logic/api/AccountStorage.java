package org.federiconafria.transfer.logic.api;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;

public interface AccountStorage {
    void insertAccount(Account account);

    Account getAccount(long id) throws EntityNotFoundException;

}
