package com.eazybytes.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CustomerDetailsDto {

    private CustomerDto customerDto;
    private AccountsDto accountsDto;
    private LoansDto loansDto;
    private CardsDto cardsDto;
}
