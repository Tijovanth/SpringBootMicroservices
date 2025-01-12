package com.eazybytes.loans.service.impl;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.mapper.LoanMapper;
import com.eazybytes.loans.repository.LoansRepository;
import com.eazybytes.loans.service.ILoansService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class LoansServiceImpl implements ILoansService {

    private LoansRepository loansRepository;

    @Override
    public void createLoan(String mobileNumber) {
        Optional<Loans> loan = loansRepository.findByMobileNumber(mobileNumber);
        if(loan.isPresent()){
            throw new LoanAlreadyExistsException("Loan is already present with this mobile number = "+ mobileNumber);
        }
        loansRepository.save(createNewLoan(mobileNumber));
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Loans loan = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Loan is not found for this mobile number = "+ mobileNumber)
        );
        LoansDto loansDto = LoanMapper.mapToLoansDto(new LoansDto(), loan);
        return loansDto;
    }

    @Override
    public boolean updateLoan(LoansDto loansDto) {
        boolean isUpdated = false;
        Loans loans = loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(() ->
                new ResourceNotFoundException("Loan is not found for this loan number = "+ loansDto.getLoanNumber())
        );
        loansRepository.save(loans);
        isUpdated = true;
        return isUpdated;
    }

    @Override
    public void deleteLoan(String mobileNumber) {
        Loans loan = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->
                new ResourceNotFoundException("Loan is not found for this mobile number = "+ mobileNumber)
        );
        loansRepository.deleteById(loan.getLoanId());
    }


    private Loans createNewLoan(String mobileNumber){
        Loans loan = new Loans();
        loan.setMobileNumber(mobileNumber);
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        loan.setLoanNumber(randomLoanNumber+"");
        loan.setTotalAmount(LoansConstants.NEW_LOAN_LIMIT);
        loan.setLoanType(LoansConstants.HOME_LOAN);
        loan.setAmountPaid(0);
        loan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return loan;
    }
}
