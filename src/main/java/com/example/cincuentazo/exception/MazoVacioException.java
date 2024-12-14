package com.example.cincuentazo.exception;

/**
 * Custom exception class that represents an empty deck error.
 * This exception is thrown when there are no cards left in the deck to draw or recycle.
 */
public class MazoVacioException extends Exception {

    /**
     * Constructor for the exception.
     * @param mensaje The message to be passed to the exception, explaining the cause or details of the error.
     */
    public MazoVacioException(String mensaje) {
        super(mensaje); // Pass the message to the superclass constructor
    }
}
