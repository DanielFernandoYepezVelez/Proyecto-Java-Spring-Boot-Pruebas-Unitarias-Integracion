package org.nextappoficial.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.TestMethodOrder;
import org.nextappoficial.test.springboot.app.dto.TransactionDto;
import org.nextappoficial.test.springboot.app.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerWebTestClientTest {
    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void transferirDineroDeUnaCuentaAOtraJsonTest() throws JsonProcessingException {
        // GIVEN (Datos De Prueba)
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBankId(1L);
        transactionDto.setAccountOriginId(1L);
        transactionDto.setAccountDestination(2L);
        transactionDto.setAmount(new BigDecimal("300"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia Exitosa");
        response.put("transaction", transactionDto);

        // WHEN
        /* TODO LO QUE TENEMOS AQUI ES PROPIO DE WEB_TEST_CLIENT */
        webTestClient.post().uri("/api/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                /*
                    En El Fondo Es Para Enviar El Request, Es El Intercambio Entre La Solucitud O Request Y La Respuesta.
                    Todo Lo Que Venga Después Del Exchange(), Es La Respuesta!
                */
                .exchange()
        // THEN
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(Matchers.is("Transferencia Exitosa"))
                .jsonPath("$.message").value(value -> {
                    assertEquals("Transferencia Exitosa", value);
                })
                .jsonPath("$.message").isEqualTo("Transferencia Exitosa")
                .jsonPath("$.transaction.accountOriginId").isEqualTo(transactionDto.getAccountOriginId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    /* Este Test Funciona Exactamente Igual Que El Superior, Solo Que Este Trabaja Con El JSON De Forma Distinta */
    @Test
    @Order(2)
    void transferirDineroDeUnaCuentaAOtraJUnitValidationConsumeTest() {
        // GIVEN (Datos De Prueba)
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBankId(1L);
        transactionDto.setAccountOriginId(1L);
        transactionDto.setAccountDestination(2L);
        transactionDto.setAmount(new BigDecimal("100"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia Exitosa");
        response.put("transaction", transactionDto);

        // WHEN
        /* TODO LO QUE TENEMOS AQUI ES PROPIO DE WEB_TEST_CLIENT */
        webTestClient.post().uri("/api/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                /*
                    En El Fondo Es Para Enviar El Request, Es El Intercambio Entre La Solucitud O Request Y La Respuesta.
                    Todo Lo Que Venga Después Del Exchange(), Es La Respuesta!
                */
                .exchange()
                // THEN
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                // Aqui Estoy Consumiendo Un JSON De Forma Diferente
                .consumeWith(result -> {
                    try {
                        /* Este Instrucción Es Muy IMPORTANTE PorQue Me Permite Acceder Y Navegar Por Los Atributos Del Json */
                        JsonNode json = objectMapper.readTree(result.getResponseBody());

                        Assertions.assertEquals("Transferencia Exitosa", json.path("message").asText());
                        Assertions.assertEquals(1L, json.path("transaction").path("accountOriginId").asLong());
                        Assertions.assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        Assertions.assertEquals("100", json.path("transaction").path("amount").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // Una Forma De Validar Con webTestClient
    @Test
    @Order(3)
    void detalleDeLaCuentaTest() {
        webTestClient.get().uri("/api/account/detail/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("Daniel")
                .jsonPath("$.balance").isEqualTo(600);
    }

    // Otra Forma De Validar Con JUnit5
    @Test
    @Order(4)
    void detlleDeLaCuenta2Test() {
        webTestClient.get().uri("/api/account/detail/2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                // Convirtiendo El Json Al Objeto Cuenta, Por Que Tienen Los Mismos Atributos
                .expectBody(Account.class)
                .consumeWith(response -> {
                    Account account = response.getResponseBody();
                    Assertions.assertEquals("Fernando", account.getName());
                    Assertions.assertEquals("2400.00", account.getBalance().toPlainString());
                });
    }

    @Test
    @Order(5)
    void listarLasCuentasComoJsonTest() {
        webTestClient.get().uri("/api/account/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Daniel")
                .jsonPath("$[0].balance").isEqualTo(600)
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Fernando")
                .jsonPath("$[1].balance").isEqualTo(2400)
                .jsonPath("$").isArray()
                .jsonPath("$").value(Matchers.hasSize(2));
    }

    @Test
    @Order(6)
    void listarLasCuentasComoListaTest() {
        webTestClient.get().uri("/api/account/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .consumeWith(response -> {
                    List<Account> accounts = response.getResponseBody();

                    // Con Assertions Puedo Validar Este Arreglo De Muchas Maneras
                    Assertions.assertNotNull(accounts);
                    Assertions.assertEquals(2, accounts.size());
                    Assertions.assertEquals(1, accounts.get(0).getId());
                    Assertions.assertEquals("Daniel", accounts.get(0).getName());
                    Assertions.assertEquals("600.0", accounts.get(0).getBalance().toPlainString());
                    Assertions.assertEquals(2, accounts.get(1).getId());
                    Assertions.assertEquals("Fernando", accounts.get(1).getName());
                    Assertions.assertEquals("2400.0", accounts.get(1).getBalance().toPlainString());
                })
                .hasSize(2)
                .value(Matchers.hasSize(2));
    }

    @Test
    @Order(7)
    void guardarListaTest() {
        // GIVEN
        Account account = new Account(null, "Pepe Nuevo", new BigDecimal("3250"));

        // WHEN
        webTestClient.post().uri("/api/account/account")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
        // THEN
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.name").isEqualTo("Pepe Nuevo")
                .jsonPath("$.balance").isEqualTo(3250);
    }

    @Test
    @Order(8)
    void guardarListaExtendTest() {
        // GIVEN
        Account account = new Account(null, "Carlos Rey", new BigDecimal("25000"));

        // WHEN
        webTestClient.post().uri("/api/account/account")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(account)
                .exchange()
                // THEN
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Account.class)
                .consumeWith(response -> {
                    /* Los Datos Del Json Se Mapean Con Los Datos Del Objeto Account */
                    Account account1 = response.getResponseBody();

                    Assertions.assertNotNull(account1);
                    Assertions.assertEquals(4, account1.getId());
                    Assertions.assertEquals("Carlos Rey", account1.getName());
                    Assertions.assertEquals("25000", account1.getBalance().toPlainString());
                });
    }

    @Test
   @Order(9)
    void eliminarCuentaIdTest() {
        //  Listo Las Cuentas
        webTestClient.get().uri("/api/account/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(4);

        // Elimino La Cuenta Por Su Respectivo ID
        webTestClient.delete().uri("/api/account/delete/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // Listo Las Cuentas Nuevamente
        webTestClient.get().uri("/api/account/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Account.class)
                .hasSize(3);

        // Busco Por El Detalles (LO COMENTAMOS PARA MANEJAR EL ERROR, DE UNA MANERA MÁS AMIGABLE PARA EL USUARIO)
        /* webTestClient.get().uri("/api/account/detail/3")
                .exchange()
                .expectStatus().is5xxServerError(); */

        webTestClient.get().uri("/api/account/detail/3")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

    }
}