package com.cydeo.banksimulation.mapper;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.enums.AccountType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountMapperTest {
    private AccountMapper accountMapper;

    @BeforeAll
    public void setUp(){
        ModelMapper modelMapper = new ModelMapper();
        accountMapper = new AccountMapper(modelMapper);
    }

    @Test
    public void should_convert_account_dto_to_account_entity(){
        AccountDTO accountDTO = prepareAccountDTO(1L, new BigDecimal(10),
                AccountStatus.DELETED,true, 123L,AccountType.SAVINGS);
        Account account = accountMapper.convertToEntity(accountDTO);
        assertEquals(accountDTO.getBalance(), account.getBalance());
        assertEquals(accountDTO.getAccountType(), account.getAccountType());
        assertEquals(accountDTO.getUserId(), account.getUserId());
    }

    @Test
    public void should_convert_account_entity_to_account_dto(){
        Account account = prepareAccount(1L, new BigDecimal(10),
                AccountStatus.DELETED,true, 123L,AccountType.SAVINGS);
        AccountDTO accountDTO = accountMapper.convertToDto(account);
        assertEquals( account.getBalance(),accountDTO.getBalance());
        assertEquals( account.getAccountType(),accountDTO.getAccountType());
        assertEquals(account.getUserId(),accountDTO.getUserId());
    }

    private AccountDTO prepareAccountDTO(Long id, BigDecimal balance,
                                         AccountStatus accountStatus,
                                         boolean verified,
                                         Long userId,
                                         AccountType accountType){
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(id);
        accountDTO.setBalance(balance);
        accountDTO.setAccountStatus(accountStatus);
        accountDTO.setOtpVerified(verified);
        accountDTO.setUserId(userId);
        accountDTO.setPhoneNumber("121165465");
        accountDTO.setCreationDate(new Date());
        accountDTO.setAccountType(accountType);
        return accountDTO;
    }

    private Account prepareAccount(Long id, BigDecimal balance,
                                         AccountStatus accountStatus,
                                         boolean verified,
                                         Long userId,
                                         AccountType accountType){
        Account account = new Account();
        account.setId(id);
        account.setBalance(balance);
        account.setAccountStatus(accountStatus);
        account.setOtpVerified(verified);
        account.setUserId(userId);
        account.setPhoneNumber("121165465");
        account.setCreationDate(new Date());
        account.setAccountType(accountType);
        return account;
    }

}
