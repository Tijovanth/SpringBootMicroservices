package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.AccountsMsgDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private static final Logger logger = LoggerFactory.getLogger(AccountsServiceImpl.class);
    AccountsRepository accountsRepository;
    CustomerRepository customerRepository;
    StreamBridge streamBridge;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        if(customerRepository.findByMobileNumber(customerDto.getMobileNumber()).isPresent()){
            throw new CustomerAlreadyExistsException("Customer Already exists exception "+customer.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        Accounts savedAccounts = accountsRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccounts,savedCustomer);
    }

    private void sendCommunication(Accounts accounts, Customer customer){
        var accountsMsgDto = new AccountsMsgDto(accounts.getAccountNumber(),customer.getName(),customer.getEmail(),customer.getMobileNumber());
        logger.info("Send Communication Processing");
        var result = streamBridge.send("sendCommunication-out-0",accountsMsgDto);
        logger.info("Send Communication Processed successfully? "+ result);
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
            new ResourceNotFoundException("Customer" , "mobileNumber" , mobileNumber)
        );

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(() ->
                new ResourceNotFoundException("Accounts" , "customerId" , String.valueOf(customer.getCustomerId()))
        );
        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(new AccountsDto(),accounts);
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer,new CustomerDto());
        customerDto.setAccountsDto(accountsDto);
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto != null){
            accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(() ->
                    new ResourceNotFoundException("Accounts","Account Number",accountsDto.getAccountNumber()+"")
            );
            Accounts accounts = AccountsMapper.mapToAccounts(accountsDto,new Accounts());
            accountsRepository.save(accounts);
            customerRepository.findById(accounts.getCustomerId()).orElseThrow(() ->
                    new ResourceNotFoundException("Customer","CustomerId",accounts.getCustomerId()+"")
            );
            Customer customer = CustomerMapper.mapToCustomer(customerDto,new Customer());
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Customer" , "mobileNumber" , mobileNumber)
        );
        customerRepository.deleteById(customer.getCustomerId());
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdate = false;
        if(accountNumber != null){
            Accounts accounts = accountsRepository.findById(accountNumber).orElseThrow(() ->
                    new ResourceNotFoundException("Accounts","Account Number",accountNumber+"")
            );
            accounts.setCommunicationsSW(true);
            accountsRepository.save(accounts);
            isUpdate = true;
        }
        return isUpdate;
    }


    private Accounts createNewAccount(Customer customer){
        Accounts accounts = new Accounts();
        accounts.setCustomerId(customer.getCustomerId());
        Long accountNumber = 1000000000L + new Random().nextInt(900000000);
        accounts.setAccountNumber(accountNumber);
        accounts.setAccountType(AccountsConstants.SAVINGS);
        accounts.setBranchAddress(AccountsConstants.ADDRESS);
        return accounts;
    }


}
