package com.cydeo.banksimulation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    @NotNull
    private AccountDTO sender;

    @NotNull
    private AccountDTO receiver;

    @NotNull
    @Positive(message = " You can not use minus numbers")
    private BigDecimal amount;

    @NotEmpty
    @Size(min = 2, max = 250)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String message;

    private Date creationDate;

}
