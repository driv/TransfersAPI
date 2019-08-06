package org.federiconafria.transfer.logic.api;

import org.federiconafria.transfer.logic.entities.Transfer;

public interface TransferQueue {
    void queueTransfer(Transfer newTransfer);
}
