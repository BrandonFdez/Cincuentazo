package com.example.cincuentazo.exception;

/**
 * Custom exception class that represents an invalid card index error.
 * This exception is thrown when an attempt is made to access a card at an invalid index.
 */
public class IndiceCartaInvalidoException extends RuntimeException {

  /**
   * Constructor for the exception.
   * @param mensaje The message to be passed to the exception, explaining the cause or details of the error.
   */
  public IndiceCartaInvalidoException(String mensaje) {
    super(mensaje); // Pass the message to the superclass constructor
  }
}
