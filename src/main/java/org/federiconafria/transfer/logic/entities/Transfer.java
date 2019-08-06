package org.federiconafria.transfer.logic.entities;


import java.util.Objects;

public class Transfer {
    private final Long id;
    private final long idSourceAccount;
    private final long idDestinationAccount;
    private final Currency amount;
    private Status status;

    Transfer(long idSourceAccount, long idDestinationAccount, Currency amount) {
        this.id = null;
        this.idSourceAccount = idSourceAccount;
        this.idDestinationAccount = idDestinationAccount;
        this.amount = amount;
        this.status = Status.PENDING;
    }

    public Transfer(long id, Transfer transfer) {
        this.id = id;
        this.idSourceAccount = transfer.idSourceAccount;
        this.idDestinationAccount = transfer.idDestinationAccount;
        this.amount = transfer.amount;
        this.status = transfer.status;
    }

    public Long getId() {
        return id;
    }

    public long getIdSourceAccount() {
        return idSourceAccount;
    }

    public long getIdDestinationAccount() {
        return idDestinationAccount;
    }

    public Currency getAmount() {
        return amount;
    }

    public void execute() {
        this.status = Status.EXECUTED;
    }

    public void fail() {
        this.status = Status.FAILED;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return idSourceAccount == transfer.idSourceAccount &&
                idDestinationAccount == transfer.idDestinationAccount &&
                Objects.equals(id, transfer.id) &&
                Objects.equals(amount, transfer.amount) &&
                Objects.equals(status, transfer.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idSourceAccount, idDestinationAccount, amount, status);
    }

    public enum Status {
        PENDING,
        EXECUTED,
        FAILED,
    }
}
