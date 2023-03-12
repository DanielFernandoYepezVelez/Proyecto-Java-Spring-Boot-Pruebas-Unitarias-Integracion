package org.nextappoficial.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.nextappoficial.test.springboot.app.Datos;
import org.nextappoficial.test.springboot.app.dto.TransactionDto;
import org.nextappoficial.test.springboot.app.models.Account;
import org.nextappoficial.test.springboot.app.services.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IAccountService accountService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /* GET */
    @Test
    void listarTodasLasCuentasTest() throws Exception {
        // GIVEN
        Mockito.when(accountService.findAll()).thenReturn(Datos.accountsAll());

        // WHEN
        mvc.perform(MockMvcRequestBuilders.get("/api/account/all").contentType(MediaType.APPLICATION_JSON))
        // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Daniel"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].balance").value("1000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Fernando"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].balance").value("2000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                // Aquí Estoy Retornando El Json Completo En String Que El Service Le Al Controlador Y Este Se Lo Va Entregar Al Cliente
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(Datos.accountsAll())));

        Mockito.verify(accountService).findAll();
    }

    /* GET */
    @Test
    void detalleCuentaTest() throws Exception {
        // GIVEN
        Mockito.when(accountService.findById(1L)).thenReturn(Datos.createAccount001().orElseThrow());

        // WHEN
        mvc.perform(MockMvcRequestBuilders.get("/api/account/detail/1").contentType(MediaType.APPLICATION_JSON))
        // THEN
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Daniel"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("1000"));

        Mockito.verify(accountService).findById(1L);
    }

    @Test
    void guardarUnaCuentaTest() throws Exception {
        // GIVEN
        // Esto Pasa Por Que El ID Viene Nulo, Pero, Cuando LLega A La Base De Datos Retorna Su ID
        //Mockito.when(accountService.save(ArgumentMatchers.any())).thenReturn(Datos.saveAccount());
        Mockito.when(accountService.save(ArgumentMatchers.any()))
                        .then(invocationOnMock -> {
                            Account account = invocationOnMock.getArgument(0);
                            account.setId(3L);
                            return account;
                        });

        // WHEN
        mvc.perform(MockMvcRequestBuilders.post("/api/account/account").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Datos.saveAccount())))
        // THEN
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Pepe Nuevo")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(3250)));

        Mockito.verify(accountService).save(ArgumentMatchers.any());
    }

    /* POST */
    @Test
    void transferirDineroCuentaTest() throws Exception {
        // GIVEN
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBankId(1L);
        transactionDto.setAccountOriginId(1L);
        transactionDto.setAccountDestination(2L);
        transactionDto.setAmount(new BigDecimal("100"));

        System.out.println("La Data Que Se Recibe En El Request => " + objectMapper.writeValueAsString(transactionDto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transferencia Exitosa");
        response.put("transaction", transactionDto);

        System.out.println("La Data Que Se Espera Entregar En El Response => " + objectMapper.writeValueAsString(response));

        // WHEN
        mvc.perform(MockMvcRequestBuilders.post("/api/account/transfer").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto)))
        // THEN
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Transferencia Exitosa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.accountOriginId").value(1L))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)));
    }
}