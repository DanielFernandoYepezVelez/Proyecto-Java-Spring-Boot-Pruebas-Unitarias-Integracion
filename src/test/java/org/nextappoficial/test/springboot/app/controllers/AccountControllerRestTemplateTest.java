package org.nextappoficial.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.nextappoficial.test.springboot.app.dto.TransactionDto;
import org.nextappoficial.test.springboot.app.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerRestTemplateTest {
    
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferirDeUnaCuentaAOtraTest() throws JsonProcessingException {
        // GIVEN
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBankId(1L);
        transactionDto.setAccountOriginId(1L);
        transactionDto.setAccountDestination(2L);
        transactionDto.setAmount(new BigDecimal("200"));

        // Me Entrega Un JSON Convertido A String
        ResponseEntity<String> response = testRestTemplate.postForEntity(("/api/account/transfer"), transactionDto, String.class);

        String jsonBody = response.getBody();
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Assertions.assertNotNull(jsonBody);
        Assertions.assertTrue(jsonBody.contains("Transferencia Exitosa"));
        Assertions.assertTrue(jsonBody.contains("{\"bankId\":1,\"accountOriginId\":1,\"accountDestination\":2,\"amount\":200}"));

        // Convertir El String JSON De Arriba, A Un JSON NODE (Me Permite Más Flexibilidad)
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        Assertions.assertEquals("Transferencia Exitosa", jsonNode.path("message").asText());
        Assertions.assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        Assertions.assertEquals("200", jsonNode.path("transaction").path("amount").asText());
        Assertions.assertEquals(1L, jsonNode.path("transaction").path("accountOriginId").asLong());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("message", "Transferencia Exitosa");
        response2.put("transaction", transactionDto);

        // Comparo Los Dos Json Como Strings
        Assertions.assertEquals(objectMapper.writeValueAsString(response2), jsonBody);
    }

    @Test
    @Order(2)
    void detalleDeLaCuentaTest() {
        ResponseEntity<Account> response = testRestTemplate.getForEntity(("/api/account/detail/1"), Account.class);
        Account account = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Assertions.assertNotNull(account);
        Assertions.assertEquals(1L, account.getId());
        Assertions.assertEquals("Daniel", account.getName());
        Assertions.assertEquals("800.00", account.getBalance().toPlainString());
        Assertions.assertEquals(new Account(1L, "Daniel", new BigDecimal("800.00")), account);
    }

    @Test
    @Order(3)
    void listarTodasLasCuentas() throws JsonProcessingException {
        ResponseEntity<Account[]> response = testRestTemplate.getForEntity("/api/account/all", Account[].class);
        List<Account> accounts = Arrays.asList(Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        // Primera Forma Con El Objeto De La Lista
        Assertions.assertNotNull(accounts);
        Assertions.assertEquals(2, accounts.size());
        Assertions.assertEquals(1L, accounts.get(0).getId());
        Assertions.assertEquals("Daniel", accounts.get(0).getName());
        Assertions.assertEquals("800.00", accounts.get(0).getBalance().toPlainString());

        Assertions.assertEquals(2L, accounts.get(1).getId());
        Assertions.assertEquals("Fernando", accounts.get(1).getName());
        Assertions.assertEquals("2200.00", accounts.get(1).getBalance().toPlainString());

        // Segunda Forma Con El Objeto JSON Flexible
        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(response.getBody()));

        Assertions.assertNotNull(jsonNode);
        Assertions.assertEquals(1L, jsonNode.get(0).path("id").asLong());
        Assertions.assertEquals("Daniel", jsonNode.get(0).path("name").asText());
        Assertions.assertEquals("800.0", jsonNode.get(0).path("balance").asText());
        Assertions.assertEquals(2L, jsonNode.get(1).path("id").asLong());
        Assertions.assertEquals("Fernando", jsonNode.get(1).path("name").asText());
        Assertions.assertEquals("2200.0", jsonNode.get(1).path("balance").asText());
    }

    @Test
    @Order(4)
    void guadarUnaCuentaTest() {
        Account account = new Account(null, "Pepa", new BigDecimal("3250"));
        ResponseEntity<Account> response = testRestTemplate.postForEntity("/api/account/account", account, Account.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        Account responseBody = response.getBody();

        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(3, responseBody.getId());
        Assertions.assertEquals("Pepa", responseBody.getName());
        Assertions.assertEquals("3250", responseBody.getBalance().toPlainString());
    }

    @Test
    @Order(5)
    void eliminarCuentaTest() {
        ResponseEntity<Account[]> response = testRestTemplate.getForEntity("/api/account/all", Account[].class);
        List<Account> accounts = Arrays.asList(Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(3, accounts.size());
        /* ============================= */

        testRestTemplate.delete("/api/account/delete/3");

        response = testRestTemplate.getForEntity("/api/account/all", Account[].class);
        accounts = Arrays.asList(Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(2, accounts.size());
        /* ============================= */

        ResponseEntity<Account> responseDetalle = testRestTemplate.getForEntity(("/api/account/detail/3"), Account.class);
        Account account = responseDetalle.getBody();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseDetalle.getStatusCode());
        Assertions.assertFalse(responseDetalle.hasBody());
    }

    @Test
    @Order(6)
    void eliminarCuentaOtraFormaTest() {
        ResponseEntity<Account[]> response = testRestTemplate.getForEntity("/api/account/all", Account[].class);
        List<Account> accounts = Arrays.asList(Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(2, accounts.size());
        /* ============================= */

        Map<String, Long> pathVariable = new HashMap<>();
        pathVariable.put("idAccount", 2L);

        //ResponseEntity<Void> responseDelete = testRestTemplate.exchange("/api/account/detail/3", HttpMethod.DELETE, null, Void.class);
        ResponseEntity<Void> responseDelete = testRestTemplate.exchange("/api/account/delete/{idAccount}",
                HttpMethod.DELETE, null, Void.class, pathVariable);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseDelete.getStatusCode());
        Assertions.assertFalse(responseDelete.hasBody());

        response = testRestTemplate.getForEntity("/api/account/all", Account[].class);
        accounts = Arrays.asList(Objects.requireNonNull(response.getBody()));

        Assertions.assertEquals(1, accounts.size());
        /* ============================= */

        ResponseEntity<Account> responseDetalle = testRestTemplate.getForEntity(("/api/account/detail/3"), Account.class);
        Account account = responseDetalle.getBody();

        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseDetalle.getStatusCode());
        Assertions.assertFalse(responseDetalle.hasBody());
    }
}