package com.eazybytes.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LoansDto {

    @NotEmpty(message = "Mobile Number can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits")
    private String mobileNumber;

    @NotEmpty(message = "LoanType can not be a null or empty")
    private String loanType;

    @NotEmpty(message = "Loan Number can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{12})",message = "LoanNumber must be 12 digits")
    private String loanNumber;

    @Positive(message = "Total loan amount should be greater than zero")
    private int totalAmount;

    @PositiveOrZero(message = "Total loan amount paid should be equal or greater than zero")
    private int outstandingAmount;

    @PositiveOrZero(message = "Total outstanding amount should be equal or greater than zero")
    private int amountPaid;
}
