package org.mycode.exceptions;

public class NotUniquePrimaryKeyException extends Exception {
    public NotUniquePrimaryKeyException() {
        super();
    }
    public NotUniquePrimaryKeyException(String message) {
        super(message);
    }
}
