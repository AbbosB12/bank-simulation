package com.cydeo.banksimulation.it;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@Sql(scripts = "insert-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AccountServiceIT {
    @Autowired
    private AccountService accountService;

    @Test
    public void should_retrieve_account_by_id(){
        AccountDTO accountDTO = accountService.retrieveById(1L);
        assertEquals(123L,accountDTO.getUserId());
    }

    @Test
    public void should_list_active_accounts(){
        List<AccountDTO> accountDTOS  = accountService.listAllActiveAccount();
        assertEquals(3,accountDTOS.size());
    }

    @Test
    public void should_delete_account(){
        List<AccountDTO> accounts  = accountService.listAllActiveAccount();
        assertEquals(3,accounts.size());

        AccountDTO accountDTO = accountService.deleteAccount(1L);
        assertEquals(AccountStatus.DELETED,accountDTO.getAccountStatus());

        List<AccountDTO> accountDTOS  = accountService.listAllActiveAccount();
        assertEquals(2,accountDTOS.size());
    }

}
