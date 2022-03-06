package com.cydeo.banksimulation.repository;

import com.cydeo.banksimulation.entity.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountRepository {

    public static List<Account> accountList = new ArrayList<>();

    public Account save(Account account) {
        accountList.add(account);
        return account;

    }

}
