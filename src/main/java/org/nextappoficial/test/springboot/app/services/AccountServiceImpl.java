package org.nextappoficial.test.springboot.app.services;

import org.nextappoficial.test.springboot.app.models.Account;
import org.nextappoficial.test.springboot.app.models.Bank;
import org.nextappoficial.test.springboot.app.repositories.IAccountRepository;
import org.nextappoficial.test.springboot.app.repositories.IBankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements IAccountService {

    private IAccountRepository accountRepository;
    private IBankRepository bankRepository;

    public AccountServiceImpl(IAccountRepository accountRepository, IBankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteById(Long idAccount) {
        accountRepository.deleteById(idAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public int reviewTotalTransfer(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();
        return bank.getTotalTransfer();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal reviewBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        return account.getBalance();
    }

    @Override
    @Transactional
    public void transfer(Long bankId, Long numberAccountOrigin, Long numberAccountDestination, BigDecimal amount) {
        Account accountOrigin = accountRepository.findById(numberAccountOrigin).orElseThrow();
        accountOrigin.debit(amount);
        accountRepository.save(accountOrigin);

        Account accountDestination = accountRepository.findById(numberAccountDestination).orElseThrow();
        accountDestination.credit(amount);
        accountRepository.save(accountDestination);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfer = bank.getTotalTransfer();
        bank.setTotalTransfer(++totalTransfer);
        bankRepository.save(bank);
    }
}
