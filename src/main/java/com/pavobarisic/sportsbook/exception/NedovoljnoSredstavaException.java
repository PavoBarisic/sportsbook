package com.pavobarisic.sportsbook.exception;

public class NedovoljnoSredstavaException extends RuntimeException {

    public NedovoljnoSredstavaException(String poruka) {
        super(poruka);
    }
}