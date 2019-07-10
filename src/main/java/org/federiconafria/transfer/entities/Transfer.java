package org.federiconafria.transfer.entities;

import java.util.Currency;
import java.util.Date;

public class Transfer {
    long id;
    long idSourceAccount;
    long idDestinationAccount;
    Currency amount;
    String status;
    Date creationDate;
    Date executionDate;


    public Transfer(long id, long idSourceAccount, long idDestinationAccount, Currency amount, String status) {
        this.id = id;
        this.idSourceAccount = idSourceAccount;
        this.idDestinationAccount = idDestinationAccount;
        this.amount = amount;
        this.status = "PENDING";
        this.creationDate = new Date();
    }

    public enum Status {
        PENDING,
        EXECUTED,
        FAILED,
    }
}
