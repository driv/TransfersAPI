package org.federiconafria.transfer.logic.entities;

import java.util.Objects;

public class Account {
    private final String user;
    private Currency balance;
    private final long id;

    Account(String user, Currency initialAmount) {
        this.user = user;
        this.balance = initialAmount;
        this.id = 0;
    }

    public Account(long id, Account toCopy) {
        this.id = id;
        this.user = toCopy.user;
        this.balance = toCopy.balance;
    }

    public Account(Account toCopy) {
        this.id = toCopy.id;
        this.user = toCopy.user;
        this.balance = toCopy.balance;
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public Currency getBalance() {
        return balance;
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

    public void withdraw(Currency amount) {
        this.balance = Currency.valueOf(this.getBalance().getAmount().subtract(amount.getAmount()));
    }

    public void deposit(Currency amount) {
        this.balance = Currency.valueOf(this.getBalance().getAmount().add(amount.getAmount()));
    }
}
