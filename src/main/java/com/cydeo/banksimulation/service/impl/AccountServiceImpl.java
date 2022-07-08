package com.cydeo.banksimulation.service.impl;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.entity.Account;
import com.cydeo.banksimulation.enums.AccountStatus;
import com.cydeo.banksimulation.mapper.AccountMapper;
import com.cydeo.banksimulation.repository.AccountRepository;
import com.cydeo.banksimulation.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public List<AccountDTO> listAllAccount() {
        List<Account> accountList = accountRepository.findAll();
        return accountList.stream().map(accountMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void createNewAccount(AccountDTO accountDTO) {
        accountDTO.setCreationDate(new Date());
        accountDTO.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(accountMapper.convertToEntity(accountDTO));
    }


    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.getById(accountId);
        account.setAccountStatus(AccountStatus.DELETED);
        accountRepository.save(account);
    }

    @Override
    public AccountDTO retrieveById(Long account) {
        return accountMapper.convertToDto(accountRepository.getById(account));
    }

    @Override
    public List<AccountDTO> listAllActiveAccount() {
        List<Account> accountList = accountRepository.findAllByAccountStatus(AccountStatus.ACTIVE);
        return accountList.stream().map(accountMapper::convertToDto).collect(Collectors.toList());
    }

}
