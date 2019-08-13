package org.federiconafria.transfer.logic.entities;

import org.federiconafria.transfer.logic.exceptions.EntityCreationException;

import java.math.BigDecimal;

public class TransferBuilder {
    private long idSourceAccount;
    private long idDestinationAccount;
    private Currency amount;

    public TransferBuilder setIdSourceAccount(long idSourceAccount) {
        this.idSourceAccount = idSourceAccount;
        return this;
    }

    public TransferBuilder setIdDestinationAccount(long idDestinationAccount) {
        this.idDestinationAccount = idDestinationAccount;
        return this;
    }

    public TransferBuilder setAmount(String amount) {
        this.amount = Currency.valueOf(amount);
        return this;
    }

    public Transfer build() throws EntityCreationException {
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new EntityCreationException("Transfer amount should be positive");
        if (idSourceAccount == idDestinationAccount)
            throw new EntityCreationException("Transfer should be to a different account");
        return new Transfer(idSourceAccount, idDestinationAccount, amount);
    }
}