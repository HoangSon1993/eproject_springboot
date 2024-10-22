package com.sontung.eproject_springboot.exception;

public class PriceChangedException extends RuntimeException {
    public PriceChangedException(String message) {
        super(message);
    }
}
