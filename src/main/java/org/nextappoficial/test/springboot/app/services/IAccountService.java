package org.nextappoficial.test.springboot.app.services;

import org.nextappoficial.test.springboot.app.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface IAccountService {

    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    void deleteById(Long idAccount);
    int reviewTotalTransfer(Long bankId);
    BigDecimal reviewBalance(Long accountId);
    void transfer(Long bankId, Long numberAccountOrigin, Long numberAccountDestination, BigDecimal amount);

}
