package org.federiconafria.entities;

import java.util.Objects;

public class Account implements Cloneable {
    private final String user;
    private final Currency amount;
    private final long id;

    public Account(String user, Currency initialAmount) {
        this.user = user;
        this.amount = initialAmount;
        this.id = 0;
    }

    public Account(long id, Account toCopy) {
        this.id = id;
        this.user = toCopy.user;
        this.amount = toCopy.amount;
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public Currency getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return id == account.id &&
                getUser().equals(account.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), id);
    }
}
