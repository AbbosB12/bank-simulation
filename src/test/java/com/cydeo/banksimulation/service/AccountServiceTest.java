package com.cydeo.banksimulation.service;


import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.dto.OtpDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.enums.AccountType;
import com.cydeo.banksimulation.exception.AccountStatusInvalidException;
import com.cydeo.banksimulation.exception.BalanceNotSufficientException;
import com.cydeo.banksimulation.mapper.AccountMapper;
import com.cydeo.banksimulation.repository.AccountRepository;
import com.cydeo.banksimulation.service.impl.AccountServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void should_create_new_account(){
        AccountDTO accountDTO = prepareAccountDTOForBalanceTest(new BigDecimal(40), AccountStatus.ACTIVE);
        Account account = new Account();
        OtpDTO otpDTO = new OtpDTO();
        otpDTO.setOtpCode(123456);
        otpDTO.setOtpId(1L);

        when(accountMapper.convertToEntity(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(otpService.createOtpSendSms(account)).thenReturn(otpDTO);

        Throwable throwable = catchThrowable(() -> accountService.createNewAccount(accountDTO));
        assertNull(throwable);
    }

    @Test
    public void should_throw_balance_not_sufficient_exception_when_balance_equal_null(){
        AccountDTO accountDTO = prepareAccountDTOForBalanceTest(null, AccountStatus.ACTIVE);

        Throwable throwable = ThrowableAssert.catchThrowable(()-> accountService.createNewAccount(accountDTO));

        assertInstanceOf(BalanceNotSufficientException.class,throwable);
        BalanceNotSufficientException balanceNotSufficientException = (BalanceNotSufficientException) throwable;
        assertEquals("Initial balance needs to bigger than Zero", balanceNotSufficientException.getMessage());
    }

    @Test
    public void should_throw_balance_not_sufficient_exception_when_balance_equal_zero(){
        AccountDTO accountDTO = prepareAccountDTOForBalanceTest(new BigDecimal(0),AccountStatus.ACTIVE);

        Throwable throwable = ThrowableAssert.catchThrowable(()-> accountService.createNewAccount(accountDTO));

        assertInstanceOf(BalanceNotSufficientException.class,throwable);
        BalanceNotSufficientException balanceNotSufficientException = (BalanceNotSufficientException) throwable;
        assertEquals("Initial balance needs to bigger than Zero", balanceNotSufficientException.getMessage());
    }

    @Test
    public void should_throw_balance_not_sufficient_exception_when_account_status_deleted(){
        AccountDTO accountDTO = prepareAccountDTOForBalanceTest(new BigDecimal(12), AccountStatus.DELETED);

        Throwable throwable = ThrowableAssert.catchThrowable(()-> accountService.createNewAccount(accountDTO));

        assertInstanceOf(AccountStatusInvalidException.class,throwable);
        AccountStatusInvalidException accountStatusInvalidException = (AccountStatusInvalidException) throwable;
        assertEquals("Account status can not be Deleted", accountStatusInvalidException.getMessage());
    }

    public AccountDTO prepareAccountDTOForBalanceTest(BigDecimal balance, AccountStatus accountStatus){
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountStatus(accountStatus);
        accountDTO.setAccountType(AccountType.CHECKINGS);
        accountDTO.setBalance(balance);
        accountDTO.setUserId(123L);
        accountDTO.setOtpVerified(false);
        return accountDTO;
    }


}
