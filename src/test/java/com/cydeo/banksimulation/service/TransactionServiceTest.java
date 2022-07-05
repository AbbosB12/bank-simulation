package com.cydeo.banksimulation.service;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.entity.Transaction;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.enums.AccountType;
import com.cydeo.banksimulation.exception.BadRequestException;
import com.cydeo.banksimulation.mapper.TransactionMapper;
import com.cydeo.banksimulation.repository.TransactionRepository;
import com.cydeo.banksimulation.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void should_make_transfer(){
        AccountDTO sender = prepareAccountDTO(1L,new BigDecimal(250),AccountStatus.ACTIVE, true, 123L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        Transaction transaction = new Transaction();

        when(accountService.retrieveById(1L)).thenReturn(sender);
        when(accountService.retrieveById(2L)).thenReturn(receiver);
        when(transactionMapper.convertToEntity(any())).thenReturn(transaction);
        when(transactionRepository.save(any())).thenReturn(transaction);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNull(throwable);
    }

    @Test
    public void should_throw_bad_request_exception_when_sender_account_is_null(){
        AccountDTO receiver = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), null, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(BadRequestException.class, throwable);
        BadRequestException badRequestException = (BadRequestException) throwable;
        assertEquals("Sender or receiver can not be null", badRequestException.getMessage());
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
}
