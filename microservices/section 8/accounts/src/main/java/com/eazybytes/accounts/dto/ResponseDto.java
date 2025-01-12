package com.eazybytes.accounts.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    private String statusCode;
    private String statusMsg;
}
