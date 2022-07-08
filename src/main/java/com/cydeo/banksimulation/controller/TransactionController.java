package com.cydeo.banksimulation.controller;

import com.cydeo.banksimulation.dto.AccountDTO;
import com.cydeo.banksimulation.dto.TransactionDTO;
import com.cydeo.banksimulation.service.AccountService;
import com.cydeo.banksimulation.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/make-transfer")
    public String retrieveTransactionForm(Model model) {

        model.addAttribute("accounts", accountService.listAllActiveAccount());
        model.addAttribute("transactionDTO", new TransactionDTO());
        model.addAttribute("lastTransactionList", transactionService.retrieveLastTransactions());

        return "transaction/make-transfer";

    }

    @PostMapping("/transfer")
    public String makeTransfer(@Valid @ModelAttribute("transactionDTO") TransactionDTO transactionDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountService.listAllAccount());
            return "transaction/make-transfer";
        }

        AccountDTO reciever = transactionDTO.getReceiver();
        AccountDTO sender = transactionDTO.getSender();
        transactionService.makeTransfer(transactionDTO.getAmount(), new Date(), sender, reciever, transactionDTO.getMessage());
        return "redirect:/make-transfer";

    }

    @GetMapping("/transaction/{id}")
    public String transactionDetailById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("transactionList", transactionService.findTransactionListByAccountId(id));
        return "transaction/transactions";
    }

}
