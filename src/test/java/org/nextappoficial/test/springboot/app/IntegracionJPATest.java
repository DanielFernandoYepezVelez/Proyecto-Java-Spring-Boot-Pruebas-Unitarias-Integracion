package org.nextappoficial.test.springboot.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.nextappoficial.test.springboot.app.models.Account;
import org.nextappoficial.test.springboot.app.repositories.IAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/* ESTO ES UNA PRUEBA DE INTEGRACIÓN ENTRE MIS REPOSITORIES Y MI BASE DE DATOS */
/* AQUI PODEMOS VISUALIZAR QUE EL CRUD FUNCIONA Y ESTA PROBADO CORRECTAMENTE */
/* El RollBack Veo Que Se Ejecuta Al Final Cuando Ejecuto Toda Mi Clase */
@DataJpaTest
public class IntegracionJPATest {

    @Autowired
    IAccountRepository accountRepository;

    @Test
    void buscarCuentaPorIdDesdeDBTest() {
        Optional<Account> account = accountRepository.findById(1L);

        Assertions.assertTrue(account.isPresent());
        Assertions.assertEquals("Daniel", account.get().getName());
    }

    @Test
    void burcarPorNombrePersonaDesdeDBTest() {
       Optional<Account> account = accountRepository.findByName("Fernando");

       Assertions.assertTrue(account.isPresent());
       Assertions.assertEquals("Fernando", account.get().getName());
    }

    @Test
    void buscarPorNombrePersonaDesdeDBNoExisteTest() {
        Optional<Account> account = accountRepository.findByName("Hola");

        Assertions.assertThrows(NoSuchElementException.class, account::orElseThrow);
        Assertions.assertFalse(account.isPresent());
    }

    @Test
    void buscarTodasLasCuentaDesdeDBTest() {
        List<Account> accounts =  accountRepository.findAll();

        Assertions.assertFalse(accounts.isEmpty());
        Assertions.assertEquals(2, accounts.size());
    }

    @Test
    void creandoUnaNuevaCuentaParaDB1Test() {
        /* GIVEN */
        Account accountOne = new Account(null, "Pepe", new BigDecimal("3000"));
        accountRepository.save(accountOne);

        /* WHEN */
        Account accountPepe = accountRepository.findByName("Pepe").orElseThrow();

        /* THEN */
        Assertions.assertEquals("Pepe", accountPepe.getName());
        Assertions.assertEquals("3000", accountPepe.getBalance().toPlainString());
        Assertions.assertEquals(3, accountPepe.getId());
    }

    @Test
    void creandoUnaNuevaCuentaParaDB2Test() {
        /* GIVEN */
        Account accountOne = new Account(null, "Pepe Dos", new BigDecimal("3250"));
        accountRepository.save(accountOne);

        /* WHEN */
        /* El Inconveniente Es Que, El ID Es Uno Cuando Se Ejecuta Toda La Clase
        * Y Es Otro Cuando Se Ejecuta Solo Este Metodo, Por Eso No Es Recomendable POR QUE
        * ES INCREMENTAL */
        Account accountPepeDos = accountRepository.findById(4L).orElseThrow();

        /* THEN */
        Assertions.assertEquals("Pepe Dos", accountPepeDos.getName());
        Assertions.assertEquals("3250", accountPepeDos.getBalance().toPlainString());
        Assertions.assertEquals(4, accountPepeDos.getId());
    }

    @Test
    void creandoUnaNuevaCuentaParaDB3Test() {
        /* GIVEN */
        Account accountOne = new Account(null, "Pepe Tres", new BigDecimal("6000"));

        /* WHEN */
        Account accountPepeTres = accountRepository.save(accountOne);

        /* THEN */
        Assertions.assertEquals("Pepe Tres", accountPepeTres.getName());
        Assertions.assertEquals("6000", accountPepeTres.getBalance().toPlainString());

        /* El Inconveniente Es Que, El ID Es Uno Cuando Se Ejecuta Toda La Clase
         * Y Es Otro Cuando Se Ejecuta Solo Este Metodo, Por Eso No Es Recomendable POR QUE
         * ES INCREMENTAL */
        Assertions.assertEquals(5, accountPepeTres.getId());
    }

    void actualizandoUnaNuevaCuentaParaDBTest() {
        /* GIVEN */
        Account accountOne = new Account(null, "Pepe Tres", new BigDecimal("6000"));

        /* WHEN */
        Account accountPepeTres = accountRepository.save(accountOne);

        /* THEN */
        Assertions.assertEquals("Pepe Tres", accountPepeTres.getName());
        Assertions.assertEquals("6000", accountPepeTres.getBalance().toPlainString());
        Assertions.assertEquals(3, accountPepeTres.getId());

        /* Proceso De Actualización */
        /* WHEN */
        accountPepeTres.setName("Juan Camilo Carmona Betancur");
        accountPepeTres.setBalance(new BigDecimal("1250"));
        Account accountDB = accountRepository.save(accountPepeTres);

        /* THEN */
        Assertions.assertEquals("Juan Camilo Carmona Betancur", accountDB.getName());
        Assertions.assertEquals("1250", accountDB.getBalance().toPlainString());
        Assertions.assertEquals(3, accountDB.getId());
    }

    @Test
    void EliminandoUnaNuevaCuentaParaDBTest() {
        Account account = accountRepository.findById(2L).orElseThrow();
        Assertions.assertEquals("Fernando", account.getName());

        accountRepository.delete(account);

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            //accountRepository.findByName("Fernando").orElseThrow();
            accountRepository.findById(2L).orElseThrow();
        });

        Assertions.assertEquals(1, accountRepository.findAll().size());
    }
}