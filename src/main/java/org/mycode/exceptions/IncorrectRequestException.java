package org.mycode.exceptions;

public class IncorrectRequestException extends Exception {
    public IncorrectRequestException() {
        super();
    }
    public IncorrectRequestException(String message) {
        super(message);
    }
}
