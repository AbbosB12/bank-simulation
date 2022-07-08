package com.cydeo.banksimulation.controller;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.dto.AccountResponseDTO;
import com.cydeo.banksimulation.dto.OtpDTO;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.enums.AccountType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void should_create_account() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountStatus(AccountStatus.ACTIVE);
        accountDTO.setAccountType(AccountType.CHECKINGS);
        accountDTO.setBalance(new BigDecimal(10));
        accountDTO.setUserId(123L);
        accountDTO.setPhoneNumber("112423423423");

        Gson gson = new Gson();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/account")
                .content(gson.toJson(accountDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").
                        value("Account is successfully created with non verified")).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

        AccountResponseDTO accountResponseDTO = gson.fromJson(
                mvcResult.getResponse().getContentAsString(), AccountResponseDTO.class);
        OtpDTO otpDTO = accountResponseDTO.getData();

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/otp")
                .content(gson.toJson(otpDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
