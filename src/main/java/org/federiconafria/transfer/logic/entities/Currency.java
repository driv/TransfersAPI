package org.federiconafria.transfer.logic.entities;

import java.math.BigDecimal;

public class Currency {
    private final BigDecimal amount;

    private Currency(BigDecimal amount) {
        this.amount = amount;
    }

    public static Currency valueOf(String amount) {
        return new Currency(new BigDecimal(amount));
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
