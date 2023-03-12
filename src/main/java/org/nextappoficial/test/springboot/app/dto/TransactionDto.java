package org.nextappoficial.test.springboot.app.dto;

import java.math.BigDecimal;

public class TransactionDto {
    private Long bankId;
    private Long accountOriginId;
    private Long accountDestination;
    private BigDecimal amount;

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getAccountOriginId() {
        return accountOriginId;
    }

    public void setAccountOriginId(Long accountOriginId) {
        this.accountOriginId = accountOriginId;
    }

    public Long getAccountDestination() {
        return accountDestination;
    }

    public void setAccountDestination(Long accountDestination) {
        this.accountDestination = accountDestination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
