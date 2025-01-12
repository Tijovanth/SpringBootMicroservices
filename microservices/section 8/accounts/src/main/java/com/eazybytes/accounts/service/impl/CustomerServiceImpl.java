package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.*;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.ICustomerService;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoan(correlationId, mobileNumber);

        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();

        customerDetailsDto.setCustomerDto(CustomerMapper.mapToCustomerDto(customer, new CustomerDto()));
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(new AccountsDto(), accounts));
        if(cardsDtoResponseEntity != null){
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }
        if(loansDtoResponseEntity != null) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }
        return customerDetailsDto;
    }
}
