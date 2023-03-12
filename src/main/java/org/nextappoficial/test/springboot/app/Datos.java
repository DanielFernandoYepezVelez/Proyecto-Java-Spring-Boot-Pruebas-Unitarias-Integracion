package org.nextappoficial.test.springboot.app;

import org.nextappoficial.test.springboot.app.models.Account;
import org.nextappoficial.test.springboot.app.models.Bank;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Datos {
    //public static final Account ACCOUNT_001 = new Account(1L, "Daniel", new BigDecimal("1000"));
    //public static final Account ACCOUNT_002 = new Account(2L, "Fernando", new BigDecimal("2000"));
    //public static final Bank BANK = new Bank(1L, "El Banco Financiero", 0);

    public static Optional<Account> createAccount001() {
        return Optional.of(new Account(1L, "Daniel", new BigDecimal("1000")));
    }

    public static Optional<Account> createAccount002() {
        return Optional.of(new Account(2L, "Fernando", new BigDecimal("2000")));
    }

    public static Optional<Bank> createBank() {
        return Optional.of(new Bank(1L, "El Banco Financiero", 0));
    }

    public static List<Account> accountsAll() {
        return Arrays.asList(
                createAccount001().orElseThrow(),
                createAccount002().orElseThrow()
        );
    }

    public static Account saveAccount() {
        return new Account(null, "Pepe Nuevo", new BigDecimal("3250"));
    }
}
