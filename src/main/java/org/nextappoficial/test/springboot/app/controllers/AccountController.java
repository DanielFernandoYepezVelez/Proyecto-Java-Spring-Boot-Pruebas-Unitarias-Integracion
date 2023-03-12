package org.nextappoficial.test.springboot.app.controllers;

import org.nextappoficial.test.springboot.app.dto.TransactionDto;
import org.nextappoficial.test.springboot.app.models.Account;
import org.nextappoficial.test.springboot.app.services.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    IAccountService accountService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> all() {
        return accountService.findAll();
    }

    @GetMapping("/detail/{idAccount}")
    public ResponseEntity<?> detail(@PathVariable Long idAccount) {
        Account account;

        try {
            account = accountService.findById(idAccount);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(account);
    }

    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }

    @DeleteMapping("/delete/{idAccount}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long idAccount) {
        accountService.deleteById(idAccount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferir(@RequestBody TransactionDto transactionDto) {
        accountService.transfer(transactionDto.getBankId(),
                                transactionDto.getAccountOriginId(),
                                transactionDto.getAccountDestination(),
                                transactionDto.getAmount());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia Exitosa");
        response.put("transaction", transactionDto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
