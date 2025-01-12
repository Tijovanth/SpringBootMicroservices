package com.eazybytes.accounts.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String entity, String field, String value) {
        super(String.format("%s with this %s = %s is not found",entity,field,value));
    }
}
