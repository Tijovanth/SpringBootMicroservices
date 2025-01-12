package com.eazybytes.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallBackController {

    @GetMapping("/contactSupport")
    public Mono<String> contactSupport(){
        return Mono.just("An error occured please try again after sometime");
    }
}
