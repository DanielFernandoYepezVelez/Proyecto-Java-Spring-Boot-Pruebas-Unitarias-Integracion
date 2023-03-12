package org.nextappoficial.test.springboot.app.repositories;

import org.nextappoficial.test.springboot.app.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IAccountRepository extends JpaRepository<Account, Long> {
    //List<Account> findAll();
    //Account findById(Long id);
    //void update(Account account);

    @Query("select account from Account account where account.name = ?1")
    Optional<Account> findByName(String name);

}