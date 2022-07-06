package com.cydeo.banksimulation.service;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.entity.Transaction;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.enums.AccountType;
import com.cydeo.banksimulation.exception.AccountNotVerifiedException;
import com.cydeo.banksimulation.exception.AccountOwnerShipException;
import com.cydeo.banksimulation.exception.BadRequestException;
import com.cydeo.banksimulation.exception.BalanceNotSufficientException;
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

    @Test
    public void should_throw_bad_request_exception_when_receiver_account_is_null(){
        AccountDTO sender = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, null, "message"));
        assertNotNull(throwable);
        assertInstanceOf(BadRequestException.class, throwable);
        BadRequestException badRequestException = (BadRequestException) throwable;
        assertEquals("Sender or receiver can not be null", badRequestException.getMessage());
    }

    @Test
    public void should_throw_bad_request_exception_when_sender_and_receiver_account_id_is_same(){
        AccountDTO sender = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(BadRequestException.class, throwable);
        BadRequestException badRequestException = (BadRequestException) throwable;
        assertEquals("Sender account needs to be different from recaiver account", badRequestException.getMessage());
    }

    @Test
    public void should_throw_bad_request_exception_when_sender_account_status_is_deleted(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.DELETED, true, 125L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(BadRequestException.class, throwable);
        BadRequestException badRequestException = (BadRequestException) throwable;
        assertEquals("Sender account is deleted, you can not send money from this account", badRequestException.getMessage());
    }

    @Test
    public void should_throw_bad_request_exception_when_receiver_account_status_is_deleted(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.DELETED, true, 125L, AccountType.CHECKINGS);
        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(BadRequestException.class, throwable);
        BadRequestException badRequestException = (BadRequestException) throwable;
        assertEquals("Receiver account is deleted, you can not send money to this account", badRequestException.getMessage());
    }

    @Test
    public void should_throw_account_not_verified_exception_sender_account_is_not_verified(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.ACTIVE, false, 125L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        when(accountService.retrieveById(2L)).thenReturn(sender);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(AccountNotVerifiedException.class, throwable);
        AccountNotVerifiedException accountNotVerifiedException = (AccountNotVerifiedException) throwable;
        assertEquals("account not verified yet.", accountNotVerifiedException.getMessage());
    }

    @Test
    public void should_throw_account_not_verified_exception_receiver_account_is_not_verified(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150),AccountStatus.ACTIVE, true, 125L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150),AccountStatus.ACTIVE, false, 125L, AccountType.CHECKINGS);
        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNotNull(throwable);
        assertInstanceOf(AccountNotVerifiedException.class, throwable);
        AccountNotVerifiedException accountNotVerifiedException = (AccountNotVerifiedException) throwable;
        assertEquals("account not verified yet.", accountNotVerifiedException.getMessage());
    }

    @Test
    public void should_throw_account_ownership_exception_when_sender_account_is_saving_but_user_id_is_different(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 16L, AccountType.CHECKINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));

        assertNotNull(throwable);
        assertInstanceOf(AccountOwnerShipException.class, throwable);
        AccountOwnerShipException accountOwnerShipException = (AccountOwnerShipException) throwable;
        assertEquals("When one of the account type is SAVINGS, sender and receiver has tobe same person"
                , accountOwnerShipException.getMessage());
    }

    @Test
    public void should_throw_account_ownership_exception_when_receiver_account_is_saving_but_user_id_is_different(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 16L, AccountType.SAVINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));

        assertNotNull(throwable);
        assertInstanceOf(AccountOwnerShipException.class, throwable);
        AccountOwnerShipException accountOwnerShipException = (AccountOwnerShipException) throwable;
        assertEquals("When one of the account type is SAVINGS, sender and receiver has tobe same person"
                , accountOwnerShipException.getMessage());
    }

    @Test
    public void should_throw_account_ownership_exception_when_both_account_are_saving_but_user_id_is_different(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 16L, AccountType.SAVINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));

        assertNotNull(throwable);
        assertInstanceOf(AccountOwnerShipException.class, throwable);
        AccountOwnerShipException accountOwnerShipException = (AccountOwnerShipException) throwable;
        assertEquals("When one of the account type is SAVINGS, sender and receiver has tobe same person"
                , accountOwnerShipException.getMessage());
    }

    @Test
    public void should_make_transfer_when_both_account_are_saving_but_user_id_is_same(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNull(throwable);
    }

    @Test
    public void should_make_transfer_when_sender_account_is_saving_but_user_id_is_same(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.CHECKINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNull(throwable);
    }

    @Test
    public void should_make_transfer_when_receiver_account_is_saving_but_user_id_is_same(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));
        assertNull(throwable);
    }

    @Test
    public void should_throw_balance_not_sufficient_exception_when_sender_balance_is_not_enough(){
        AccountDTO sender = prepareAccountDTO(2L,new BigDecimal(5)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.CHECKINGS);
        AccountDTO receiver = prepareAccountDTO(5L,new BigDecimal(150)
                ,AccountStatus.ACTIVE, true, 15L, AccountType.SAVINGS);

        when(accountService.retrieveById(2L)).thenReturn(sender);
        when(accountService.retrieveById(5L)).thenReturn(receiver);

        Throwable throwable = catchThrowable(() -> transactionService.makeTransfer(BigDecimal.TEN
                ,new Date(), sender, receiver, "message"));

        assertNotNull(throwable);
        assertInstanceOf(BalanceNotSufficientException.class, throwable);
        BalanceNotSufficientException balanceNotSufficientException = (BalanceNotSufficientException) throwable;
        assertEquals("Balance is not enough for this transaction"
                , balanceNotSufficientException.getMessage());

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
