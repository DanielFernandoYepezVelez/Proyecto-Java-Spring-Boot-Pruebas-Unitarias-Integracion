package org.nextappoficial.test.springboot.app.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.nextappoficial.test.springboot.app.exceptions.InsufficientMoneyEception;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal balance;

    public Account() {
    }

    public Account(Long id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void debit(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientMoneyEception("Dinero Insuficiente En La Cuenta");
        }

        this.balance = newBalance;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(name, account.name) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, balance);
    }
}
