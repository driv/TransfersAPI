package org.federiconafria.transfer.logic.api;

import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;

public interface TransferStorage {
    void insertTransfer(Transfer transfer);

    Transfer getTransfer(long id) throws EntityNotFoundException;
}
