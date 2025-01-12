package com.eazybytes.loans.controller;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansContactInfoDto;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.dto.ResponseDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.service.ILoansService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class LoanController {

    private ILoansService iLoansService;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private LoansContactInfoDto loansContactInfoDto;

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    public LoanController(ILoansService iLoansService){
        this.iLoansService = iLoansService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoan(@RequestParam @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits") String mobileNumber){
        iLoansService.createLoan(mobileNumber);
        ResponseDto responseDto = new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201);
        return new ResponseEntity<>(responseDto,HttpStatus.CREATED);
    }

    @GetMapping("/fetch")
    public ResponseEntity<LoansDto> fetchLoan(@RequestHeader("eazybank-correlation-id") String correlationId, @RequestParam @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits") String mobileNumber){
        logger.debug("fetch Loan Details method start");
        LoansDto loansDto = iLoansService.fetchLoan(mobileNumber);
        logger.debug("fetch Loan Details method end");
        return ResponseEntity.status(HttpStatus.OK).body(loansDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoan(@Valid  @RequestBody LoansDto loansDto){
        boolean isUpdated = iLoansService.updateLoan(loansDto);
        if(isUpdated){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(LoansConstants.STATUS_200,LoansConstants.MESSAGE_200));
        }
        return null;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoan(@RequestParam @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits") String mobileNumber){
        iLoansService.deleteLoan(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(LoansConstants.STATUS_200,LoansConstants.MESSAGE_200));
    }

    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo(){
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion(){
        return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<LoansContactInfoDto> getContactInfo(){
        logger.debug("Invoked api");
        //throw new RuntimeException();
        return ResponseEntity.status(HttpStatus.OK).body(loansContactInfoDto);
    }


}
