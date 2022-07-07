package com.cydeo.banksimulation.service.impl;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.dto.OtpDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.exception.AccountNotFoundException;
import com.cydeo.banksimulation.exception.AccountStatusInvalidException;
import com.cydeo.banksimulation.exception.BalanceNotSufficientException;
import com.cydeo.banksimulation.mapper.AccountMapper;
import com.cydeo.banksimulation.repository.AccountRepository;
import com.cydeo.banksimulation.service.AccountService;
import com.cydeo.banksimulation.service.OtpService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OtpService otpService;

    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountMapper accountMapper,
                              OtpService otpService) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.otpService = otpService;
    }

    @Override
    public List<AccountDTO> listAllAccount() {
        List<Account> accountList = accountRepository.findAll();
        return accountList.stream().map(accountMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public OtpDTO createNewAccount(AccountDTO accountDTO){
        if (accountDTO.getBalance() == null ||
                accountDTO.getBalance().compareTo(BigDecimal.ZERO) <= 0){
            throw new BalanceNotSufficientException("Initial balance needs to bigger than Zero");
        }

        if (accountDTO.getAccountStatus().equals(AccountStatus.DELETED)){
            throw new AccountStatusInvalidException("Account status can not be Deleted");
        }

        accountDTO.setCreationDate(new Date());
        accountDTO.setAccountStatus(AccountStatus.ACTIVE);
        Account account = accountMapper.convertToEntity(accountDTO);
        account.setOtpVerified(false);
        account = accountRepository.save(account);
        return otpService.createOtpSendSms(account);
    }


    @Override
    public AccountDTO deleteAccount(Long accountId) {
        Account account = accountRepository.getById(accountId);
        if (account == null){
            throw new AccountNotFoundException("Account not found");
        }
        account.setAccountStatus(AccountStatus.DELETED);
        return accountMapper.convertToDto(accountRepository.save(account));
    }


    @Override
    public AccountDTO retrieveById(Long account) {
        return accountMapper.convertToDto(accountRepository.getById(account));
    }

    @Override
    public List<AccountDTO> listAllActiveAccount() {
        List<Account> accountList = accountRepository.findAll();
        return accountList.stream().map(accountMapper::convertToDto).
                filter(accountDTO -> accountDTO.getAccountStatus()
                        .equals(AccountStatus.ACTIVE)).collect(Collectors.toList());
    }

}
