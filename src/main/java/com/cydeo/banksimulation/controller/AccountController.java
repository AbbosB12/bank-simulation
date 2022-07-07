package com.cydeo.banksimulation.controller;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.dto.OtpDTO;
import com.cydeo.banksimulation.dto.ResponseWrapper;
import com.cydeo.banksimulation.enums.AccountType;
import com.cydeo.banksimulation.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountDTO> accountList() {
        return accountService.listAllAccount();
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createAccount(@RequestBody AccountDTO accountDTO) throws Exception {
        OtpDTO otpDTO = accountService.createNewAccount(accountDTO);
        return ResponseEntity.ok(new ResponseWrapper("Account is successfully created with non verified",otpDTO , HttpStatus.OK));

    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("id") Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(new ResponseWrapper("Account is successfully deleted", HttpStatus.OK));
    }

}
