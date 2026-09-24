package com.example.ecommerce.exception;

/**
 * Exception levée lorsqu'une règle métier est violée
 * (ex: suppression d'une entité encore référencée).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
