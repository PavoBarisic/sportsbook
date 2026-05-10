package com.pavobarisic.sportsbook.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String poruka) {
        super(poruka);
    }
}