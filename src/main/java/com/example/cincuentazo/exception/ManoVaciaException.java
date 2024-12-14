package com.example.cincuentazo.exception;

/**
 * Custom exception class that represents an empty hand error.
 * This exception is thrown when a player attempts to play a move but their hand is empty.
 */
public class ManoVaciaException extends RuntimeException {

    /**
     * Constructor for the exception.
     * @param mensaje The message to be passed to the exception, explaining the cause or details of the error.
     */
    public ManoVaciaException(String mensaje) {
        super(mensaje); // Pass the message to the superclass constructor
    }
}
