package com.eazybytes.accounts.controller;


import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.service.ICustomerService;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    private final ICustomerService customerService;

    public CustomerController(ICustomerService iCustomerService){
        this.customerService = iCustomerService;
    }

    @GetMapping("/fetchCustomerDetails")
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationId, @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile Number must be 10 digits") String mobileNumber){
        logger.debug("eazyBank-correlation-id found: {} ", correlationId);
       CustomerDetailsDto customerDetailsDto =  customerService.fetchCustomerDetails(correlationId, mobileNumber);
       return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);
    }
}
