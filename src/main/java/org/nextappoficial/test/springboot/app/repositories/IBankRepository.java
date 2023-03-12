package org.nextappoficial.test.springboot.app.repositories;

import org.nextappoficial.test.springboot.app.models.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBankRepository extends JpaRepository<Bank, Long> {

    //List<Bank> findAll();
    //Bank findById(Long id);
    //void update(Bank bank);

}
