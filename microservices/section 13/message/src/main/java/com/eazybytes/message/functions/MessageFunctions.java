package com.eazybytes.message.functions;

import com.eazybytes.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {
    private static final Logger logger = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountsMsgDto,AccountsMsgDto> email(){
        return accountsMsg -> {
            logger.info("Sending email with the details "+ accountsMsg.toString());
            return accountsMsg;
        };
    }

    @Bean
    public Function<AccountsMsgDto,Long> sms(){
        return accountsMsg -> {
            logger.info("Sending sms with the details "+ accountsMsg.toString());
            return accountsMsg.accountNumber();
        };
    }
}
