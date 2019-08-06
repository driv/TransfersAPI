package org.federiconafria.transfer.logic.entities;

import org.federiconafria.transfer.logic.exceptions.EntityCreationException;

import java.math.BigDecimal;

public class AccountBuilder {
    private String user;
    private Currency amount;

    public AccountBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public AccountBuilder setAmount(String amount) {
        this.amount = Currency.valueOf(amount);
        return this;
    }

    public Account build() throws EntityCreationException {
        if (user == null) throw new EntityCreationException("Invalid user");
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new EntityCreationException("Amount should be a positive number");
        return new Account(user, amount);
    }
}